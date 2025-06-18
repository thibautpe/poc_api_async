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

# Arrêt de l'application
Write-Host "Arrêt de l'application..."
Stop-Process -Id $appProcess.Id -Force