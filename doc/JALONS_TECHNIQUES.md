# Jalons techniques du projet : évolution et choix structurants

Ce document retrace les grandes étapes techniques qui ont jalonné la construction du POC API asynchrone, en mettant l'accent sur les choix d'architecture, les patterns et l'approche itérative.

---

## 1. Initialisation et socle technique
- **Création du projet Spring Boot** : point d'entrée, contrôleur principal, premiers tests.
- **Objectif** : Disposer d'une API REST fonctionnelle, base pour l'asynchrone.

---

## 2. Asynchrone et gestion avancée du timeout
- **Ajout d'un service d'appel asynchrone** :
  - Utilisation de `CompletableFuture` pour exécuter les appels à l'API externe sans bloquer le thread principal.
  - Timeout géré via un `ScheduledExecutorService` : si l'API externe est trop lente, retour d'une erreur 504.
- **Configuration dynamique** :
  - Externalisation de l'URL de l'API externe et des paramètres de timeout dans `application.properties`.
  - Injection des pools de threads via des beans Spring.

---

## 3. Robustesse et instrumentation
- **Gestion fine des erreurs** :
  - Différenciation entre erreurs réseau, timeouts, exceptions.
  - Logging détaillé pour chaque cas (succès, timeout, erreur, réponse tardive).
- **Instrumentation métier** :
  - Ajout de compteurs et timers (Micrometer) pour suivre les succès, timeouts, erreurs, réponses tardives, etc.
  - Exposition des métriques via `/actuator/prometheus`.

---

## 4. Simulation d'API externe lente
- **Ajout d'un contrôleur et service simulant une API externe** :
  - Permet de tester le comportement asynchrone et la gestion des timeouts dans des conditions réalistes (délai paramétrable).

---

## 5. Tests de charge et validation de la résilience
- **Intégration de Gatling** :
  - Création d'un test de charge automatisé pour simuler des appels concurrents, valider la gestion des timeouts et observer la robustesse sous stress.
  - Extraction et corrélation des métriques techniques avec les résultats Gatling.

---

## 6. Industrialisation et ouverture à la production
- **Configuration avancée** :
  - Modification dynamique des ports, timeouts, taille des pools, etc.
  - Préparation à l'intégration avec Prometheus, Grafana, Signoz, Jaeger.
- **Séparation claire des responsabilités** :
  - Découpage du code en modules : contrôleur principal, service asynchrone, API externe simulée, configuration, tests unitaires et de charge.

---

## 7. Améliorations continues et refactoring
- **Refactoring pour la clarté et la maintenabilité** :
  - Simplification de la gestion des pools de threads.
  - Centralisation de la configuration.
  - Amélioration de la lisibilité du code et des logs.
- **Adaptation aux retours d'expérience** :
  - Correction de bugs, adaptation des patterns asynchrones, amélioration de la gestion des cas limites (surcharge, réponses tardives, etc.).

---

## Approche
- **Itérative** : chaque jalon technique a permis de valider une brique avant d'aller plus loin.
- **Orientée production** : architecture pensée pour être industrialisable.
- **Vibe coding** : adaptation continue, cycles courts, chaque problème rencontré a été l'occasion d'améliorer la solution technique.

---

*Ce cheminement montre comment, à partir d'un socle simple, on a construit un POC robuste, modulaire, observable et prêt à être challengé en conditions réelles.* 