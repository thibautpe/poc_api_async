---

## Introduction à Prometheus et Micrometer 

### Qu'est-ce que Prometheus ?
Prometheus est un système open source de monitoring et d'alerte. Il collecte régulièrement des métriques (compteurs, durées, etc.) exposées par les applications via un endpoint HTTP (souvent `/actuator/prometheus` dans le monde Spring Boot). Ces métriques sont stockées dans une base de données temporelle et peuvent être visualisées ou utilisées pour déclencher des alertes.

- **Fonctionnement** : Prometheus interroge périodiquement les applications ("scraping") pour récupérer les métriques.
- **Visualisation** : Les données peuvent être affichées dans Grafana ou via l'interface Prometheus.
- **Alerting** : On peut définir des règles pour être notifié en cas d'anomalie (ex : trop d'erreurs, latence élevée).

### Qu'est-ce que Micrometer ?
Micrometer est une bibliothèque Java qui permet d'instrumenter facilement une application Spring Boot pour exposer des métriques. Elle sert d'abstraction : le code reste identique, quel que soit le backend de monitoring (Prometheus, Datadog, NewRelic, etc.).

- **Intégration** : Avec Spring Boot, il suffit d'ajouter la dépendance `micrometer-registry-prometheus` pour exposer automatiquement les métriques au format Prometheus.
- **Types de métriques** :
  - **Counter** : compteur monotone (ex : nombre de requêtes)
  - **Timer/Summary** : mesure de durée (ex : temps de réponse)
  - **Gauge** : valeur instantanée (ex : taille d'une file)
- **Endpoint** : Les métriques sont exposées sur `/actuator/prometheus`.

### Pourquoi instrumenter une application ?
- Suivre la santé et la performance (latence, erreurs, timeouts...)
- Détecter les régressions ou incidents
- Aider au dimensionnement et à l'optimisation
- Fournir des alertes proactives

---

## Instrumentation et exposition des métriques dans ce projet

### 1. Ajout de Micrometer et Prometheus
- Le projet utilise Spring Boot Actuator et la dépendance Micrometer Prometheus (`micrometer-registry-prometheus`).
- Cela permet d'exposer automatiquement un endpoint `/actuator/prometheus` avec les métriques au format Prometheus.

### 2. Instrumentation du code
- Dans la classe `AsyncExternalApiCaller`, des compteurs (`Counter`) et des timers (`Timer`) sont créés via le `MeterRegistry` injecté par Spring.
- À chaque appel asynchrone à l'API externe, le code incrémente les compteurs et enregistre les durées selon le résultat (succès, erreur, timeout, réponse tardive, annulée, etc.).
- Les métriques sont nommées de façon explicite (`external_api.success.count`, `external_api.timeout.count`, etc.) pour faciliter leur exploitation.

### 3. Exposition des métriques
- Toutes les métriques sont exposées sur l'endpoint `/actuator/prometheus`.
- Après chaque test Gatling, le script PowerShell extrait et affiche ces métriques dans la console, puis ouvre le navigateur pour les visualiser en direct.

### 4. Exploitation
- Prometheus peut être configuré pour "scraper" cet endpoint à intervalle régulier.
- Les métriques peuvent être visualisées dans Grafana, utilisées pour des alertes, ou analysées pour l'amélioration continue.

---

**Dernière mise à jour : juin 2025** 