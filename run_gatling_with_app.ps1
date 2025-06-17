# Configuration
$REPORTS_DIR = "target/gatling"
$LOG_FILE = "gatling.log"
$SIMULATION_CLASS = "com.example.demo.gatling.GatlingLoadTest1"

# Arrêt du processus existant sur le port 8080
Write-Host "Arrêt du processus existant sur le port 8080..."
$processId = (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess
if ($processId) {
    Stop-Process -Id $processId -Force
    Start-Sleep -Seconds 2
}

# Démarrage de l'application
Write-Host "Démarrage de l'application..."
$appProcess = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -NoNewWindow -PassThru

# Attente du démarrage de l'application
Write-Host "Attente de 5 secondes pour le démarrage de l'application..."
Start-Sleep -Seconds 5

# Exécution du test
Write-Host "Démarrage du test Gatling..."
mvn gatling:test "-Dgatling.simulationClass=$SIMULATION_CLASS"

# Attente de la fin du test
Write-Host "Attente de 10 secondes après les tests..."
Start-Sleep -Seconds 10

# Arrêt de l'application
Write-Host "Arrêt de l'application..."
Stop-Process -Id $appProcess.Id -Force