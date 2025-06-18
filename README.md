# POC API Asynchrone avec Timeout (Spring Boot, Gatling)

Ce projet est un Proof of Concept (POC) démontrant la mise en œuvre d'une API asynchrone en Java Spring Boot, avec gestion avancée des timeouts côté appelant, simulation d'API externe lente, et tests de charge automatisés avec Gatling.

## Prérequis

- Java 11 ou supérieur
- Maven 3.6 ou supérieur

## Installation

1. Cloner le projet
2. Se placer dans le répertoire du projet
3. Compiler le projet :
```bash
mvn clean install
```

## Lancer l'application

Pour démarrer l'application sur le port 8081 :
```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse : http://localhost:8081

## Endpoints disponibles

### Endpoint principal
- URL : `/handler`
- Méthode : GET
- Paramètres :
  - `delay` : délai en millisecondes (optionnel, défaut : 0)
- Exemple : http://localhost:8081/handler?delay=100

## Tests manuels

Vous pouvez tester l'API manuellement avec curl :

```bash
# Test sans délai
curl http://localhost:8081/handler

# Test avec un délai de 100ms
curl http://localhost:8081/handler?delay=100

# Test avec un délai de 500ms
curl http://localhost:8081/handler?delay=500
```

## Tests automatisés

Pour exécuter les tests unitaires :
```bash
mvn test
```

## Tests de charge avec Gatling

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

## Consultation du dashboard Gatling

Après chaque test de charge, Gatling génère automatiquement un rapport HTML interactif.

### Où trouver le rapport ?

Le rapport se trouve dans le dossier :
```
target/gatling/[nom-du-test]-[timestamp]/index.html
```
Ouvrez ce fichier dans votre navigateur pour accéder au dashboard.

### Que contient le dashboard ?
- **Nombre total de requêtes** (OK/KO)
- **Temps de réponse** (min, max, moyenne, percentiles)
- **Taux d'erreur**
- **Débit (requêtes/seconde)**
- **Distribution des temps de réponse**
- **Graphiques d'évolution dans le temps**
- **Répartition des erreurs**

### Conseils d'analyse
- Surveillez les courbes de latence et les pics d'erreur
- Analysez les percentiles (95e, 99e) pour détecter les lenteurs
- Vérifiez la stabilité du débit
- Utilisez les graphiques pour comparer plusieurs campagnes

### Exemple de visualisation
![Exemple dashboard Gatling](https://gatling.io/docs/current/img/report/response_time_distribution.png)

Pour des analyses avancées, vous pouvez exporter les résultats ou les intégrer dans des outils comme Grafana, Kibana ou Excel.

## Structure du projet

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

## Configuration

Le fichier `application.properties` contient la configuration de base :
- Port : 8081
- Logging : configuré pour afficher les IDs de requête et les durées

## Logs

Les logs incluent :
- ID de requête (format : 4 chiffres)
- Délai de traitement
- Durée totale de la requête
- Nombre de requêtes actives 

## Gestion des timeouts et erreurs HTTP 504

### Côté serveur
- Si l'API externe met plus de 2000 ms à répondre, le contrôleur retourne une réponse JSON avec le code HTTP 504 (Gateway Timeout) :

```json
{
  "requestId": "0001",
  "error": "EXTERNAL_API_TIMEOUT",
  "message": "Request timeout after 2000ms (requested delay: 2300ms)",
  "status": "TIMEOUT"
}
```

### Côté Gatling
- Les réponses HTTP 504 sont désormais comptabilisées comme des erreurs (KO) dans les rapports Gatling.
- Seules les réponses HTTP 200 sont considérées comme des succès (OK).
- Vous pouvez retrouver le détail des erreurs dans la section "Errors" du rapport Gatling, par exemple :

```
> status.find.is(200), but actually found 504   15 (65%)
```

- Le taux d'erreur affiché dans le dashboard Gatling reflète donc bien les timeouts comme des échecs. 

## 📋 Critique du POC API Asynchrone

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

### Points d'amélioration ou d'attention

1. **Gestion des ressources (threads/executor)**  
   Le POC crée un nouvel `ExecutorService` et un `ScheduledExecutorService` à chaque appel.  
   → En production, il vaut mieux injecter un pool partagé (via Spring ou config) pour éviter la fuite de threads.

2. **Nettoyage des threads**  
   Les `ScheduledExecutorService` sont shutdown après usage, mais attention à ne pas en créer trop en charge réelle.

3. **Gestion des exceptions**  
   La gestion des exceptions est bonne, mais tu pourrais affiner les types d'erreurs pour distinguer timeout, erreurs réseau, etc.

4. **Extensibilité**  
   Pour un usage réel, prévoir la configuration du pool, du timeout, et de l'URL externe via des propriétés Spring.

5. **Observabilité**  
   Pour la prod, ajouter des métriques (Micrometer/Prometheus) sur les timeouts, les réponses tardives, etc.

6. **Sécurité**  
   Pour un POC ce n'est pas critique, mais en prod, attention à la validation des entrées et à la gestion des erreurs exposées.

7. **Tests**  
   Les tests sont bien faits pour le POC. Pour la prod, ajouter des tests de montée en charge plus longs, et des tests de résilience (coupure réseau, etc.).

8. **Code style**  
   Le code est propre, bien commenté, et les imports sont bien gérés.

---

### En résumé

- **Pour un POC, la solution est très propre, pédagogique et réaliste.**
- **Pour un passage en production**, il faudra industrialiser la gestion des pools de threads, la configuration, la sécurité, et l'observabilité.

Si besoin d'exemples de refactoring pour la prod, ou d'approfondir un point (ex : injection du pool, gestion avancée des timeouts, monitoring), voir avec l'équipe ou demander un accompagnement technique. 