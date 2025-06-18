<#
Script : run_gatling_with_app.ps1

Ce script PowerShell automatise l'exécution d'un test de charge Gatling sur l'application Spring Boot, l'extraction des métriques Micrometer/Prometheus, et l'ouverture d'un navigateur pour visualiser ces métriques.

---

## Fonctionnalités principales
- Arrête tout processus existant sur le port cible (par défaut 8080)
- Démarre l'application Spring Boot sur le port choisi
- Attend que l'application soit prête
- Lance le test Gatling (simulation Scala définie dans le projet)
- Attend la fin du test et un délai supplémentaire pour la stabilisation
- Extrait et affiche dans la console toutes les métriques Micrometer/Prometheus pertinentes (succès, erreurs, timeouts, réponses tardives, durées, etc.)
- Ouvre automatiquement Google Chrome sur l'URL des métriques Prometheus (`/actuator/prometheus`)
- Attend la fermeture de la fenêtre Chrome avant d'arrêter l'application Spring Boot

---

## Prérequis
- PowerShell (Windows)
- Maven installé et accessible dans le PATH
- Google Chrome installé et accessible via `chrome.exe` dans le PATH
- Port 8080 (ou celui configuré) disponible

---

## Utilisation
```powershell
./run_gatling_with_app.ps1
```

- Le script est interactif : il attend la fermeture de la fenêtre Chrome avant d'arrêter l'application.
- Les métriques sont affichées dans la console et consultables dans le navigateur.
- Les rapports Gatling sont générés dans `target/gatling/`.

---

## Personnalisation
- Modifiez `$TARGET_PORT` pour changer le port utilisé.
- Modifiez le chemin du navigateur si besoin.
- Adaptez la liste des métriques extraites selon vos besoins.

---

# FIN DE LA DOCUMENTATION
#>
# Configuration
$REPORTS_DIR = "target/gatling"
$LOG_FILE = "gatling.log"
$SIMULATION_CLASS = "com.example.demo.GatlingLoadTest1"
$TARGET_PORT = 8080  # Modifier ici si besoin

# Arrêt du processus existant sur le port cible
Write-Host "Arrêt du processus existant sur le port $TARGET_PORT..."
$processId = (Get-NetTCPConnection -LocalPort $TARGET_PORT -ErrorAction SilentlyContinue).OwningProcess
if ($processId) {
    Stop-Process -Id $processId -Force
    Start-Sleep -Seconds 2
}

# Démarrage de l'application
Write-Host "Démarrage de l'application sur le port $TARGET_PORT..."
$appProcess = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run -Dserver.port=$TARGET_PORT" -NoNewWindow -PassThru

# Attente du démarrage effectif du port
Write-Host "Attente du démarrage effectif du port $TARGET_PORT..."
$maxWait = 30
$waited = 0
while ($waited -lt $maxWait) {
    $tcp = Get-NetTCPConnection -LocalPort $TARGET_PORT -ErrorAction SilentlyContinue
    if ($tcp) { break }
    Start-Sleep -Seconds 1
    $waited++
}
if ($waited -ge $maxWait) {
    Write-Host "Le port $TARGET_PORT n'est pas disponible après $maxWait secondes. Arrêt."
    Stop-Process -Id $appProcess.Id -Force
    exit 1
}

# Exécution du test Gatling avec le port cible en variable d'environnement
Write-Host "Démarrage du test Gatling sur le port $TARGET_PORT..."
$env:GATLING_TARGET_PORT = "$TARGET_PORT"
Start-Process -FilePath "mvn" -ArgumentList "gatling:test", "-Dgatling.simulationClass=$SIMULATION_CLASS", "-Dserver.port=$TARGET_PORT" -NoNewWindow -Wait

# Attente de la fin du test
Write-Host "Attente de 10 secondes après les tests..."
Start-Sleep -Seconds 10

# Extraction des métriques Micrometer détaillées
Write-Host "Extraction des métriques Micrometer (toutes catégories)..."
try {
    $metrics = Invoke-RestMethod -Uri "http://localhost:$TARGET_PORT/actuator/prometheus"
    $metricNames = @(
        "external_api_success_count",
        "external_api_error_count",
        "external_api_timeout_count",
        "external_api_late_response_count",
        "external_api_total_count",
        "external_api_http_2xx_count",
        "external_api_http_4xx_count",
        "external_api_http_5xx_count",
        "external_api_cancelled_count",
        "external_api_all_duration_seconds",
        "external_api_success_duration_seconds",
        "external_api_error_duration_seconds",
        "external_api_timeout_duration_seconds",
        "external_api_late_response_duration_seconds",
        "external_api_cancelled_duration_seconds"
    )
    foreach ($name in $metricNames) {
        $metrics -split "`n" | Where-Object { $_ -match $name } | ForEach-Object { Write-Host $_ }
    }
} catch {
    Write-Host "Impossible de récupérer les métriques Micrometer. L'application doit être en cours d'exécution sur le port $TARGET_PORT."
}

# Ouvre Chrome sur l'URL des métriques et attend la fermeture
Write-Host "Ouverture de Google Chrome sur les métriques Prometheus... (fermez la fenêtre pour arrêter l'application)"
$browserProcess = Start-Process "chrome.exe" -ArgumentList "--new-window http://localhost:$TARGET_PORT/actuator/prometheus" -PassThru
$browserProcess.WaitForExit()

# Arrêt de l'application
Write-Host "Arrêt de l'application..."
Stop-Process -Id $appProcess.Id -Force