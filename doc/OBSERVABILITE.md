# Observabilité : généralités, concepts et mise en œuvre dans ce projet

## 1. Généralités sur l'observabilité

L'observabilité désigne la capacité à comprendre l'état interne d'un système à partir de ses sorties externes. C'est un pilier essentiel pour le maintien en conditions opérationnelles, la détection proactive d'incidents, l'analyse de performance et l'amélioration continue.

L'observabilité moderne repose sur trois piliers principaux :
- **Logs** : traces textuelles d'événements, d'erreurs, d'exceptions, etc.
- **Métriques** : valeurs numériques agrégées (compteurs, durées, taux d'erreur, etc.)
- **Traces** : suivi du parcours d'une requête à travers les différents composants d'un système distribué

À cela s'ajoutent souvent :
- **Alerting** : notifications automatiques en cas d'anomalie
- **Dashboards** : visualisation synthétique et temps réel

## 2. Notions clés

- **Logs** : utiles pour le debug, l'audit, la recherche d'incidents. Doivent être structurés et centralisés pour être exploitables à grande échelle.
- **Métriques** : permettent de suivre la santé, la performance, la charge, les erreurs, etc. Idéales pour le monitoring temps réel et l'alerting.
- **Traces distribuées** : indispensables pour comprendre les latences et les dépendances dans les architectures microservices.
- **Alertes** : doivent être pertinentes, actionnables, et éviter le bruit.
- **Dashboards** : facilitent la prise de décision rapide et la communication avec les équipes.

## 3. Observabilité dans ce projet

Ce POC met l'accent sur la collecte et l'exploitation des **métriques** :
- Instrumentation du code avec **Micrometer** (compteurs, timers, etc.)
- Exposition des métriques au format **Prometheus** via `/actuator/prometheus`
- Extraction automatique des métriques après chaque test Gatling (PowerShell)
- Visualisation possible dans le navigateur ou via Prometheus/Grafana
- Suivi des succès, erreurs, timeouts, réponses tardives, latences, etc.

Des logs sont également présents (Spring Boot, loggers personnalisés) pour le suivi des événements et erreurs.

## 4. Pistes pour aller plus loin

- **Traces distribuées** : intégrer OpenTelemetry ou Spring Cloud Sleuth pour suivre le parcours complet d'une requête (correlationId, spanId, etc.)
- **Centralisation des logs** : utiliser ELK (Elasticsearch, Logstash, Kibana) ou Grafana Loki pour agréger et rechercher les logs
- **Alerting Prometheus** : définir des règles d'alerte sur les métriques critiques (taux d'erreur, timeouts, saturation, etc.)
- **Dashboards Grafana** : créer des dashboards croisant métriques techniques et métier pour une vision globale
- **Métriques personnalisées** : ajouter des tags (endpoint, type d'erreur, etc.) pour des analyses plus fines
- **Tests de chaos/fiabilité** : intégrer des outils comme Chaos Monkey pour tester la résilience
- **SLO/SLA** : formaliser des objectifs de service et les monitorer automatiquement

## 5. Références utiles
- [Qu'est-ce que l'observabilité ? (Grafana)](https://grafana.com/docs/grafana/latest/observability/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer](https://micrometer.io/)
- [Prometheus](https://prometheus.io/)
- [OpenTelemetry](https://opentelemetry.io/)
- [ELK Stack](https://www.elastic.co/what-is/elk-stack) 