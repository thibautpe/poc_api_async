# Script de configuration pour Signoz Cloud
Write-Host "☁️ Configuration OpenTelemetry + Signoz Cloud" -ForegroundColor Green
Write-Host ""

Write-Host "📋 Étapes pour configurer Signoz Cloud :" -ForegroundColor Yellow
Write-Host "1. Aller sur https://cloud.signoz.io" -ForegroundColor Cyan
Write-Host "2. Créer un compte gratuit" -ForegroundColor Cyan
Write-Host "3. Créer un nouveau projet" -ForegroundColor Cyan
Write-Host "4. Copier l'endpoint OTLP fourni" -ForegroundColor Cyan
Write-Host ""

Write-Host "🔧 Configuration actuelle dans application.properties :" -ForegroundColor Yellow
Write-Host "otel.exporter.otlp.endpoint=https://ingest.us.signoz.cloud:443" -ForegroundColor Gray
Write-Host ""

Write-Host "💡 Pour personnaliser l'endpoint :" -ForegroundColor Yellow
Write-Host "- Modifier la propriété otel.exporter.otlp.endpoint dans application.properties" -ForegroundColor Gray
Write-Host "- Remplacer par l'endpoint fourni par Signoz Cloud" -ForegroundColor Gray
Write-Host ""

Write-Host "🚀 Pour tester :" -ForegroundColor Yellow
Write-Host "1. Lancer l'application : mvn spring-boot:run" -ForegroundColor Cyan
Write-Host "2. Faire quelques appels à l'API" -ForegroundColor Cyan
Write-Host "3. Voir les traces dans l'interface Signoz Cloud" -ForegroundColor Cyan
Write-Host ""

Write-Host "📊 Avantages de Signoz Cloud :" -ForegroundColor Green
Write-Host "✅ Pas d'installation locale" -ForegroundColor Green
Write-Host "✅ Interface moderne et complète" -ForegroundColor Green
Write-Host "✅ Stockage et maintenance gérés" -ForegroundColor Green
Write-Host "✅ Plan gratuit généreux" -ForegroundColor Green
Write-Host ""

Write-Host "🔗 Liens utiles :" -ForegroundColor Cyan
Write-Host "- Signoz Cloud : https://cloud.signoz.io" -ForegroundColor Blue
Write-Host "- Documentation : https://signoz.io/docs/" -ForegroundColor Blue
Write-Host "- OpenTelemetry : https://opentelemetry.io/" -ForegroundColor Blue 