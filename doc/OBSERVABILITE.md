# Observabilité : généralités, concepts et mise en œuvre dans ce projet

## 1. Introduction et piliers de l'observabilité

- **Logs** : traces textuelles d'événements, d'erreurs, d'exceptions.
- **Métriques** : valeurs numériques agrégées (compteurs, durées, taux d'erreur, etc.).
- **Traces distribuées** : suivi du parcours d'une requête à travers les différents composants d'un système distribué.
- **Alerting** et **Dashboards** : notifications automatiques et visualisation synthétique.

---

## 2. Observabilité dans ce projet

- **Métriques** instrumentées avec Micrometer, exposées via Prometheus (`/actuator/prometheus`).
- **Logs** via Spring Boot et loggers personnalisés.
- **Extraction automatique** des métriques après chaque test Gatling.
- **Visualisation** possible dans Prometheus, Grafana, ou le navigateur.
- **Suivi** des succès, erreurs, timeouts, réponses tardives, latences, etc.

> **Voir [PROMETHEUS.md](PROMETHEUS.md) pour les détails sur l'instrumentation et l'exposition des métriques.**

---

## 3. OpenTelemetry : instrumentation et export des traces/métriques

OpenTelemetry est la solution standard pour instrumenter les applications modernes (traces, métriques, logs) et exporter ces données vers des outils d'observabilité (Signoz, Jaeger, Prometheus, etc.).

### Pourquoi OpenTelemetry ?
- Collecte automatique des traces et métriques (requêtes HTTP, accès DB, etc.).
- Compatible avec de nombreux backends via OTLP.
- Découplé du code métier : configuration principalement via le classpath et les propriétés.

### Dépendances nécessaires (déjà présentes)
- `io.opentelemetry:opentelemetry-api`
- `io.opentelemetry:opentelemetry-sdk`
- `io.opentelemetry:opentelemetry-exporter-otlp`
- `io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter`

### Exemple de configuration (`application.properties`)
```
# Export OTLP (exemple pour Signoz local)
otel.exporter.otlp.endpoint=http://localhost:4317
otel.resource.attributes=service.name=poc-api-async
otel.metrics.exporter=otlp
otel.traces.exporter=otlp
otel.exporter.otlp.protocol=grpc

# Pour exporter uniquement les traces
# otel.metrics.exporter=none
```
- Pour Signoz Cloud, utiliser l'endpoint fourni par Signoz.
- Pour Jaeger ou Tempo, adapter l'endpoint OTLP selon le backend.

### Démarrage
- Lancer le backend d'observabilité (Signoz, Jaeger, etc.).
- Démarrer l'application Spring Boot : l'instrumentation et l'export sont automatiques si la config est présente.

### Pour aller plus loin
- Ajouter des spans personnalisés dans le code avec l'API OpenTelemetry si besoin.
- [Documentation officielle OpenTelemetry Java](https://opentelemetry.io/docs/instrumentation/java/)

---

## 4. Pistes pour aller plus loin

- **Traces distribuées** : intégrer OpenTelemetry (déjà prêt) ou Spring Cloud Sleuth.
- **Centralisation des logs** : ELK (Elasticsearch, Logstash, Kibana) ou Grafana Loki.
- **Alerting Prometheus** : règles d'alerte sur les métriques critiques.
- **Dashboards Grafana** : croiser métriques techniques et métier.
- **Métriques personnalisées** : tags pour analyses fines.
- **Tests de chaos/fiabilité** : outils comme Chaos Monkey.
- **SLO/SLA** : formaliser et monitorer les objectifs de service.

---

## 5. Références utiles

- [Qu'est-ce que l'observabilité ? (Grafana)](https://grafana.com/docs/grafana/latest/observability/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer](https://micrometer.io/)
- [Prometheus](https://prometheus.io/)
- [OpenTelemetry](https://opentelemetry.io/)
- [ELK Stack](https://www.elastic.co/what-is/elk-stack)

**Dernière mise à jour : juin 2025** 