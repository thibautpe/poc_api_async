# Script de lancement Signoz avec OpenTelemetry
Write-Host "🚀 Démarrage de Signoz avec OpenTelemetry..." -ForegroundColor Green

# Vérifier que Docker est installé
try {
    docker --version | Out-Null
    Write-Host "✅ Docker détecté" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker n'est pas installé ou pas accessible" -ForegroundColor Red
    Write-Host "Veuillez installer Docker Desktop et relancer ce script" -ForegroundColor Yellow
    exit 1
}

# Lancer Signoz avec Docker Compose
Write-Host "📦 Lancement des services Signoz..." -ForegroundColor Yellow
docker-compose up -d

# Attendre que les services soient prêts
Write-Host "⏳ Attente du démarrage des services..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérifier que les services sont en cours d'exécution
Write-Host "🔍 Vérification des services..." -ForegroundColor Yellow
docker-compose ps

Write-Host ""
Write-Host "🎉 Signoz est prêt !" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Interface web Signoz: http://localhost:3000" -ForegroundColor Cyan
Write-Host "🔧 API Query Service: http://localhost:8080" -ForegroundColor Cyan
Write-Host "📈 Otel Collector: http://localhost:4317 (gRPC)" -ForegroundColor Cyan
Write-Host ""
Write-Host "💡 Pour arrêter Signoz: docker-compose down" -ForegroundColor Yellow
Write-Host "💡 Pour voir les logs: docker-compose logs -f" -ForegroundColor Yellow 