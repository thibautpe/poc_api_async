# Installation manuelle de Signoz sans Docker

## Prérequis
- Java 11+
- Node.js 16+
- Go 1.19+
- ClickHouse (base de données)

## Étape 1 : Installer ClickHouse

### Windows
1. Télécharger ClickHouse depuis https://clickhouse.com/docs/en/install#install-from-deb-packages
2. Installer le serveur ClickHouse
3. Créer la base de données :
```sql
CREATE DATABASE signoz_traces;
```

### Linux
```bash
sudo apt-get install clickhouse-server clickhouse-client
sudo systemctl start clickhouse-server
```

## Étape 2 : Installer l'Otel Collector

1. Télécharger l'Otel Collector depuis https://github.com/open-telemetry/opentelemetry-collector/releases
2. Utiliser la configuration `otel-collector-config.yaml` déjà créée
3. Lancer l'Otel Collector :
```bash
./otelcol-contrib --config=otel-collector-config.yaml
```

## Étape 3 : Installer Signoz Query Service

1. Cloner le repo Signoz :
```bash
git clone https://github.com/SigNoz/signoz.git
cd signoz/query-service
```

2. Configurer les variables d'environnement :
```bash
export SIGNOZ_DB_DSN="clickhouse://localhost:9000?database=signoz_traces"
export SIGNOZ_REDIS_URL="redis://localhost:6379"
```

3. Compiler et lancer :
```bash
go mod download
go run cmd/main.go
```

## Étape 4 : Installer Signoz Frontend

1. Dans le dossier `frontend` :
```bash
cd ../frontend
npm install
npm start
```

## Étape 5 : Configuration de l'application

Utiliser la configuration dans `application.properties` qui pointe vers l'Otel Collector local :
```properties
otel.exporter.otlp.endpoint=http://localhost:4317
```

## Ports utilisés
- ClickHouse : 9000 (gRPC), 8123 (HTTP)
- Otel Collector : 4317 (OTLP gRPC)
- Query Service : 8080
- Frontend : 3000

## Difficultés
- Installation complexe de ClickHouse
- Configuration manuelle de tous les services
- Gestion des dépendances entre services
- Maintenance plus difficile

## Recommandation
Utiliser Signoz Cloud (Option 1) pour un démarrage rapide sans complexité d'installation. 