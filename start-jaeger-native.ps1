# Script pour lancer Jaeger en binaire natif (sans Docker)
Write-Host "🚀 Téléchargement et lancement de Jaeger en binaire natif..." -ForegroundColor Green

# Créer le dossier jaeger s'il n'existe pas
$jaegerDir = "jaeger"
if (!(Test-Path $jaegerDir)) {
    New-Item -ItemType Directory -Path $jaegerDir
    Write-Host "📁 Dossier jaeger créé" -ForegroundColor Green
}

# URL de téléchargement Jaeger pour Windows
$jaegerUrl = "https://github.com/jaegertracing/jaeger/releases/download/v1.57.0/jaeger-1.57.0-windows-amd64.tar.gz"
$jaegerTar = "jaeger.tar.gz"
$jaegerExe = "jaeger-all-in-one.exe"

# Vérifier si Jaeger est déjà téléchargé
if (!(Test-Path "$jaegerDir\$jaegerExe")) {
    Write-Host "📥 Téléchargement de Jaeger..." -ForegroundColor Yellow
    
    # Télécharger Jaeger
    Invoke-WebRequest -Uri $jaegerUrl -OutFile $jaegerTar
    
    # Extraire l'archive
    Write-Host "📦 Extraction de l'archive..." -ForegroundColor Yellow
    tar -xzf $jaegerTar -C $jaegerDir
    
    # Nettoyer
    Remove-Item $jaegerTar
    Write-Host "✅ Jaeger téléchargé et extrait" -ForegroundColor Green
} else {
    Write-Host "✅ Jaeger déjà présent" -ForegroundColor Green
}

# Lancer Jaeger
Write-Host "🚀 Lancement de Jaeger..." -ForegroundColor Yellow
Write-Host "📊 Interface web: http://localhost:16686" -ForegroundColor Cyan
Write-Host "📈 OTLP endpoint: http://localhost:4317" -ForegroundColor Cyan
Write-Host ""
Write-Host "💡 Appuyez sur Ctrl+C pour arrêter Jaeger" -ForegroundColor Yellow

# Lancer Jaeger avec OTLP activé
Set-Location $jaegerDir
.\$jaegerExe --collector.otlp.enabled=true 