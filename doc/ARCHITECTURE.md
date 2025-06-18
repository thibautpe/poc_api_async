# Document d'architecture – Projet API Asynchrone

## 1. Introduction

Ce document présente l'architecture du projet "API Asynchrone". Il décrit les choix techniques, l'organisation des composants, les flux principaux, ainsi que les points d'extension et de configuration.

## 2. Vue d'ensemble de l'architecture

- **Type d'application** : API REST asynchrone basée sur Spring Boot
- **Langage** : Java 11
- **Principaux modules** :
  - Contrôleurs REST
  - Services asynchrones
  - Simulation d'API externe
  - Tests de charge (Gatling)
  - Observabilité (Prometheus, logs)

### Schéma global

```
[Client] → [Controller REST] → [Service Asynchrone] → [API Externe simulée]
                                 ↓
                        [Gestion des timeouts]
                                 ↓
                            [Réponse au client]
```

## 3. Choix techniques

- **Framework principal** : Spring Boot
- **Asynchrone** : Utilisation de `CompletableFuture` et d'exécuteurs personnalisés
- **Tests de charge** : Gatling (scénarios dans `run_gatling_with_app.ps1`)
- **Monitoring** : Prometheus, logs Spring
- **Gestion de la configuration** : `application.properties`

## 4. Description des modules

### 4.1 Contrôleurs REST
- Gèrent les endpoints d'entrée (voir `HandlerController`, `ExternalApiController`)

### 4.2 Services asynchrones
- Logique métier asynchrone (voir `AsyncExternalApiCaller`, `ExternalApiService`)
- Gestion des délais, timeouts, pool de threads

### 4.3 Simulation d'API externe
- Endpoint simulant un service distant, avec délais configurables

### 4.4 Tests de charge
- Scripts Gatling pour simuler des charges et mesurer la performance

### 4.5 Observabilité
- Exposition de métriques Prometheus
- Logs applicatifs

## 5. Flux principaux

### 5.1 Appel asynchrone
1. Le client appelle un endpoint REST
2. Le contrôleur délègue au service asynchrone
3. Le service appelle l'API externe simulée (asynchrone)
4. Gestion du timeout et retour de la réponse

### 5.2 Gestion des erreurs
- Timeout, erreurs d'appel externe, gestion centralisée des exceptions

## 6. Tests et observabilité

- **Tests unitaires** : présents dans `src/test/java`
- **Tests de charge** : Gatling (`run_gatling_with_app.ps1`)
- **Monitoring** : Prometheus, logs

## 7. Points d'extension et configuration

- Ajouter un endpoint : créer un nouveau contrôleur ou méthode dans un contrôleur existant
- Ajouter un service : implémenter une nouvelle classe de service et l'injecter
- Configurer les délais/timeouts : modifier `application.properties`

## 8. Contraintes et limites

- **Performance** : dépend du pool de threads et de la gestion des timeouts
- **Sécurité** : endpoints non sécurisés par défaut (à adapter selon besoin)
- **Scalabilité** : limitée par la configuration du pool et la machine hôte

## 9. Annexes

- Diagrammes complémentaires (voir `doc/CLASS_DIAGRAM.md`)
- Liens utiles :
  - [README.md](../README.md)
  - [PATTERNS_ASYNC.md](PATTERNS_ASYNC.md)
  - [PROMETHEUS.md](PROMETHEUS.md) 