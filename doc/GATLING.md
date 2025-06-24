# Gatling : tests de charge et performance

## Généralités

Gatling est un outil open source de test de charge et de performance pour applications web et APIs. Il permet de simuler un grand nombre d'utilisateurs virtuels, de générer du trafic HTTP réaliste, et de mesurer la robustesse, la latence et la scalabilité d'un système.

- **Langage de simulation** : Scala (DSL simple et puissant)
- **Rapports** : HTML interactifs, métriques détaillées (temps de réponse, taux d'erreur, percentiles, etc.)
- **Intégration CI/CD** : Facile à intégrer dans des pipelines d'automatisation
- **Extensible** : Supporte HTTP, WebSocket, JMS, etc.

## Principales fonctionnalités

- Simulation de scénarios complexes (boucles, pauses, conditions, feeders de données)
- Injection de charge progressive ou massive (ramp-up, constant, peak, etc.)
- Assertions sur les temps de réponse, taux de succès/erreur, etc.
- Génération automatique de rapports graphiques
- Support de l'enregistrement de scénarios via Recorder

## Utilisation dans ce projet

Dans ce POC, Gatling est utilisé pour :

- Simuler des appels concurrents à l'API asynchrone exposée par Spring Boot
- Tester la gestion des timeouts, des réponses tardives, et la robustesse du système sous charge
- Générer des rapports de performance après chaque campagne de test
- Extraire et corréler les métriques techniques (Micrometer/Prometheus) avec les résultats Gatling

Le script PowerShell `run_gatling_with_app.ps1` automatise :
- Le démarrage de l'application
- L'exécution du test Gatling
- L'extraction des métriques
- L'ouverture du rapport et des métriques dans le navigateur

## Améliorations possibles

### Scénarios plus variés (exemples)
- **Montée en charge progressive** :
```scala
setUp(
  scn.inject(rampUsers(100) during (60.seconds))
).protocols(httpProtocol)
```
Augmente progressivement le nombre d'utilisateurs de 0 à 100 sur 1 minute.

- **Pic de charge soudain** :
```scala
setUp(
  scn.inject(atOnceUsers(200))
).protocols(httpProtocol)
```
Lance 200 utilisateurs simultanés d'un coup.

- **Mix de delays** :
```scala
val feeder = csv("delays.csv").circular // delays.csv: delay,timeout
val scn = scenario("Mix delays").feed(feeder)
  .exec(http("Handle Request")
    .get("/handle-request?delay=${delay}&timeout=${timeout}")
    .check(status.is(200)))
```
Utilise un feeder CSV pour varier les delays et timeouts à chaque requête.

- **Erreurs simulées côté API externe** :
```scala
val scn = scenario("Erreurs API externe")
  .exec(http("Erreur 500")
    .get("/external-api?forceError=500")
    .check(status.is(500)))
  .pause(1)
  .exec(http("Erreur 404")
    .get("/external-api?forceError=404")
    .check(status.is(404)))
```
Simule des réponses 500 et 404 pour tester la robustesse.

- **Burst/rafale** :
```scala
setUp(
  scn.inject(nothingFor(5.seconds),
    atOnceUsers(50),
    nothingFor(10.seconds),
    atOnceUsers(50))
).protocols(httpProtocol)
```
Deux rafales de 50 utilisateurs séparées par 10 secondes de pause.

- **Scénario "utilisateur réel"** :
```scala
val scn = scenario("Parcours utilisateur")
  .exec(http("Succès")
    .get("/handle-request?delay=1000&timeout=2000").check(status.is(200)))
  .pause(1)
  .exec(http("Timeout")
    .get("/handle-request?delay=3000&timeout=2000").check(status.is(504)))
  .pause(1)
  .exec(http("Erreur API")
    .get("/external-api?forceError=500").check(status.is(500)))
```
Enchaîne succès, timeout, et erreur pour simuler un vrai parcours.

- **Test de longue durée** :
```scala
setUp(
  scn.inject(constantUsersPerSec(5) during (30.minutes))
).protocols(httpProtocol)
```
Charge constante de 5 utilisateurs/seconde pendant 30 minutes.

### Assertions avancées (exemples)
- **SLA stricts** :
```scala
assertions(
  global.responseTime.percentile3.lte(2000), // 99% < 2s
  global.failedRequests.count.is(0) // Aucun timeout > 2.5s si bien géré côté code
)
```

- **Taux d'erreur par type** :
```scala
assertions(
  forAll.failedRequests.percent.lte(5), // <5% d'échecs
  details("Handle Request").failedRequests.percent.lte(5),
  details("Erreur 500").failedRequests.percent.lte(1)
)
```

- **Latence par endpoint** :
```scala
assertions(
  details("Handle Request").responseTime.mean.lte(1500)
)
```

- **Répartition des statuts HTTP** :
```scala
assertions(
  global.successfulRequests.percent.gte(90), // >90% de 2xx
  global.failedRequests.percent.lte(2) // <2% de 4xx/5xx
)
```

- **Détection de régressions** :
```scala
// À faire côté CI/CD : comparer le rapport Gatling actuel à une baseline sauvegardée
```

- **Assertions sur les métriques techniques** :
```scala
// À faire côté Prometheus/Grafana :
// external_api_late_response_count_total < seuil
// Pas de croissance anormale de la mémoire (heap) ou du CPU
```

## Références
- [Site officiel Gatling](https://gatling.io/)
- [Documentation Gatling](https://gatling.io/docs/)
- [Exemples de simulations](https://github.com/gatling/gatling)

**Dernière mise à jour : juin 2025** 