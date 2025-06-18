# POC API Asynchrone avec Timeout (Spring Boot, Gatling)

Ce projet est un Proof of Concept (POC) démontrant la mise en œuvre d'une API asynchrone en Java Spring Boot, avec gestion avancée des timeouts côté appelant, simulation d'API externe lente, et tests de charge automatisés avec Gatling.

---

## 1. Prérequis & Installation

- Java 11 ou supérieur
- Maven 3.6 ou supérieur

### Installation
1. Cloner le projet
2. Se placer dans le répertoire du projet
3. Compiler le projet :
```bash
mvn clean install
```

---

## 2. Lancement de l'application

Pour démarrer l'application sur le port 8081 :
```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse : http://localhost:8081

---

## 3. Fonctionnalités principales & Endpoints

### Endpoint principal
- URL : `/handler`
- Méthode : GET
- Paramètres :
  - `delay` : délai en millisecondes (optionnel, défaut : 0)
- Exemple : http://localhost:8081/handler?delay=100

---

## 4. Configuration

Le fichier `application.properties` contient la configuration de base :
- Port : 8081
- Logging : configuré pour afficher les IDs de requête et les durées

---

## 5. Logs & gestion des erreurs

Les logs incluent :
- ID de requête (format : 4 chiffres)
- Délai de traitement
- Durée totale de la requête
- Nombre de requêtes actives 

### Gestion des timeouts et erreurs HTTP 504

#### Côté serveur
- Si l'API externe met plus de 2000 ms à répondre, le contrôleur retourne une réponse JSON avec le code HTTP 504 (Gateway Timeout) :

```json
{
  "requestId": "0001",
  "error": "EXTERNAL_API_TIMEOUT",
  "message": "Request timeout after 2000ms (requested delay: 2300ms)",
  "status": "TIMEOUT"
}
```

#### Côté Gatling
- Les réponses HTTP 504 sont désormais comptabilisées comme des erreurs (KO) dans les rapports Gatling.
- Seules les réponses HTTP 200 sont considérées comme des succès (OK).
- Vous pouvez retrouver le détail des erreurs dans la section "Errors" du rapport Gatling, par exemple :

```
> status.find.is(200), but actually found 504   15 (65%)
```

- Le taux d'erreur affiché dans le dashboard Gatling reflète donc bien les timeouts comme des échecs. 

---

## 6. Structure du projet

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── demo/
│   │               ├── DemoApplication.java
│   │               └── HandlerController.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/
            └── example/
                └── demo/
                    ├── DemoApplicationTests.java
                    └── HandlerSimulation.java
```

---

## 7. Tests

### 7.1 Tests manuels

Vous pouvez tester l'API manuellement avec curl :

```bash
# Test sans délai
curl http://localhost:8081/handler

# Test avec un délai de 100ms
curl http://localhost:8081/handler?delay=100

# Test avec un délai de 500ms
curl http://localhost:8081/handler?delay=500
```

### 7.2 Tests automatisés

Pour exécuter les tests unitaires :
```bash
mvn test
```

---

## 8. Tests de charge avec Gatling

Le projet inclut des tests de charge avec Gatling. Le scénario de test comprend trois phases :
1. Augmentation progressive jusqu'à 5 utilisateurs (5 secondes)
2. Charge constante de 2 requêtes par seconde (10 secondes)
3. Augmentation jusqu'à 10 utilisateurs (5 secondes)

### Lancer les tests de charge

1. S'assurer que l'application est en cours d'exécution
2. Dans un nouveau terminal, exécuter :
```bash
mvn gatling:test
```

### Consulter les résultats

Les rapports de test sont générés dans le dossier :
```
target/gatling/[nom-du-test]-[timestamp]/index.html
```

Ouvrez ce fichier HTML dans votre navigateur pour voir les résultats détaillés, incluant :
- Temps de réponse
- Débit
- Nombre de requêtes
- Taux d'erreurs
- Graphiques de performance

### Consultation du dashboard Gatling

Après chaque test de charge, Gatling génère automatiquement un rapport HTML interactif.

#### Où trouver le rapport ?

Le rapport se trouve dans le dossier :
```
target/gatling/[nom-du-test]-[timestamp]/index.html
```
Ouvrez ce fichier dans votre navigateur pour accéder au dashboard.

#### Que contient le dashboard ?
- **Nombre total de requêtes** (OK/KO)
- **Temps de réponse** (min, max, moyenne, percentiles)
- **Taux d'erreur**
- **Débit (requêtes/seconde)**
- **Distribution des temps de réponse**
- **Graphiques d'évolution dans le temps**
- **Répartition des erreurs**

#### Conseils d'analyse
- Surveillez les courbes de latence et les pics d'erreur
- Analysez les percentiles (95e, 99e) pour détecter les lenteurs
- Vérifiez la stabilité du débit
- Utilisez les graphiques pour comparer plusieurs campagnes

#### Exemple de visualisation
![Exemple dashboard Gatling](https://gatling.io/docs/current/img/report/response_time_distribution.png)

Pour des analyses avancées, vous pouvez exporter les résultats ou les intégrer dans des outils comme Grafana, Kibana ou Excel.

---

## 9. Observabilité & Prometheus

L'application expose des métriques techniques et métier au format Prometheus via l'endpoint `/actuator/prometheus`. Prometheus est un système open source de monitoring et d'alerte très utilisé pour collecter, stocker et visualiser ces métriques (souvent avec Grafana).

👉 Pour une introduction détaillée à Prometheus, son fonctionnement et son intégration avec ce projet, consultez [doc/PROMETHEUS.md](doc/PROMETHEUS.md)

## Métriques Prometheus/Micrometer exposées

L'application expose de nombreuses métriques pour l'observabilité des appels asynchrones à l'API externe. Ces métriques sont accessibles via `/actuator/prometheus`.

### Compteurs (Counters)
- `external_api.success.count` : Nombre d'appels aboutis (succès HTTP 2xx)
- `external_api.error.count` : Nombre d'appels en erreur (exception, hors timeout)
- `external_api.timeout.count` : Nombre d'appels ayant dépassé le délai (timeout)
- `external_api.late_response.count` : Nombre de réponses reçues après le timeout
- `external_api.total.count` : Nombre total d'appels tentés
- `external_api.http_2xx.count` : Nombre de réponses HTTP 2xx
- `external_api.http_4xx.count` : Nombre de réponses HTTP 4xx
- `external_api.http_5xx.count` : Nombre de réponses HTTP 5xx
- `external_api.cancelled.count` : Nombre d'appels annulés (cancelled)

### Timers (Durées)
- `external_api.all.duration` : Durée de tous les appels (succès, erreur, timeout)
- `external_api.success.duration` : Durée des appels réussis
- `external_api.error.duration` : Durée des appels en erreur
- `external_api.timeout.duration` : Durée jusqu'au timeout
- `external_api.late_response.duration` : Durée des réponses reçues après le timeout
- `external_api.cancelled.duration` : Durée des appels annulés

### Utilisation
- Ces métriques permettent de suivre la santé, la performance et la fiabilité de l'intégration asynchrone.
- Elles facilitent la création de dashboards Grafana (taux de succès, taux d'erreur, latence, timeouts, etc.).
- Elles aident à détecter les problèmes de saturation, de lenteur ou d'instabilité côté API externe. 

## Observabilité et Prometheus

L'application expose des métriques techniques et métier au format Prometheus via l'endpoint `/actuator/prometheus`. Prometheus est un système open source de monitoring et d'alerte très utilisé pour collecter, stocker et visualiser ces métriques (souvent avec Grafana).

👉 Pour une introduction détaillée à Prometheus, son fonctionnement et son intégration avec ce projet, consultez [doc/metrics_prometheus.md](doc/metrics_prometheus.md) 

---

## 10. 📋 Critique du POC API Asynchrone

### Points forts

1. **Gestion explicite du timeout côté appelant**  
   Le timeout est configurable et bien propagé jusqu'à l'appel asynchrone. La gestion du timeout via `CompletableFuture` et un `ScheduledExecutorService` est claire et robuste.

2. **Séparation des responsabilités**  
   Le contrôleur orchestre la logique métier et la gestion des erreurs. Le service d'appel externe encapsule la logique d'appel et de gestion du timeout.

3. **Tests automatisés et charge**  
   Présence de tests unitaires/IT et d'un scénario Gatling pour la charge et la robustesse. Les tests couvrent les cas de succès et d'échec (timeout).

4. **Documentation claire**  
   Les docs et README expliquent bien le fonctionnement, les paramètres, et la logique de timeout. Les rapports Gatling sont accompagnés d'un README explicatif.

5. **Logs détaillés**  
   Les logs permettent de suivre chaque étape, y compris les cas de timeout et de réponses tardives.

---

## 11. Documentation détaillée

Retrouvez dans le dossier `doc/` des explications approfondies sur les sujets clés du projet, organisées par thématique :

### Architecture et stack technique
- **[STACK_TECHNIQUE_JAVA11.md](doc/STACK_TECHNIQUE_JAVA11.md)** : Description de la stack technique utilisée (Java 11, Spring Boot, etc.) et justification des choix.
- **[CLASS_DIAGRAM.md](doc/CLASS_DIAGRAM.md)** : Diagramme de classes du projet pour comprendre l'architecture globale.

### Patterns et gestion de l'asynchrone
- **[PATTERNS_ASYNC.md](doc/PATTERNS_ASYNC.md)** : Panorama des patterns d'asynchronisme en Java/Spring, avec exemples et recommandations.
- **[POOL_CONFIGURATION.md](doc/POOL_CONFIGURATION.md)** : Explications sur la gestion des pools de threads pour l'asynchrone, bonnes pratiques et configuration dans le projet.
- **[delay_timeout_explained.md](doc/delay_timeout_explained.md)** : Détail sur la gestion des délais et timeouts dans les appels asynchrones, et leur impact sur le système.
- **[USE_CASES_ASYNC_TIMEOUT.md](doc/USE_CASES_ASYNC_TIMEOUT.md)** : Cas d'usage typiques de l'asynchrone et du timeout, et comment ils sont couverts dans ce POC.

### Observabilité (logs, métriques, traces)
- **[OBSERVABILITE.md](doc/OBSERVABILITE.md)** : Généralités sur l'observabilité, notions clés (logs, métriques, traces, alerting), ce qui est couvert dans ce projet et pistes pour aller plus loin (tracing distribué, alertes, dashboards, etc.).

### Tests de charge et performance
- **[GATLING.md](doc/GATLING.md)** : Présentation de Gatling, ses fonctionnalités, son usage dans ce projet pour tester l'asynchrone et les timeouts, et des pistes d'amélioration pour la suite.

### Analyse critique et retour d'expérience
- **[critique_du_poc.md](doc/critique_du_poc.md)** : Analyse critique du POC, limites, axes d'amélioration et points de vigilance.

Chaque document apporte un éclairage complémentaire pour approfondir la compréhension ou l'exploitation du projet.

--- 