# Stack technique & choix liés à Java 11

## Stack technique du projet

- **Langage** : Java 11
- **Framework principal** : Spring Boot 2.7.x
- **Build & gestion de dépendances** : Maven
- **Tests de charge** : Gatling (via plugin Maven)
- **Tests unitaires** : JUnit 5 (Spring Boot Test)
- **Logs** : SLF4J + Logback
- **Observabilité / Monitoring** :
  - **Micrometer** (instrumentation des métriques)
  - **Spring Boot Actuator** (exposition des endpoints techniques)
  - **Prometheus** (scraping et stockage des métriques)
  - **Extraction et visualisation** : métriques extraites automatiquement après chaque test Gatling, visualisation possible dans Prometheus/Grafana ou via le navigateur
- **Automatisation** : Script PowerShell pour enchaîner build, lancement de l'app, tests de charge, extraction et affichage des métriques

> **Voir [OBSERVABILITE.md](OBSERVABILITE.md) et [PROMETHEUS.md](PROMETHEUS.md) pour les détails sur l'observabilité.**
> **Voir [GATLING.md](GATLING.md) pour les détails sur les tests de charge.**

## Pourquoi Java 11 ?

- **Version LTS (Long Term Support)** : Java 11 est une version supportée à long terme, stable et largement adoptée en entreprise.
- **Compatibilité** : De nombreux frameworks (Spring Boot 2.x, Gatling, etc.) garantissent une compatibilité complète avec Java 11.
- **Écosystème** : Les outils de build, de test et de monitoring sont tous compatibles et éprouvés avec Java 11.

## Contraintes et choix imposés par Java 11

### 1. API asynchrone
- Utilisation de `CompletableFuture` (introduit en Java 8, mais pleinement exploité à partir de Java 11 pour la gestion moderne de l'asynchrone).
- Pas d'API réactive native (type Project Reactor ou RxJava) dans le cœur du projet pour rester simple et compatible Java 11 standard.

### 2. Syntaxe et fonctionnalités
- Pas d'utilisation des nouveautés Java 17+ (pattern matching, records, sealed classes, etc.).
- Utilisation des lambdas, streams, Optional, etc. (Java 8+).
- Pas de `var` pour l'inférence de type locale (introduit en Java 10, mais peu utilisé pour la lisibilité dans les projets d'équipe).

### 3. Dépendances
- Toutes les dépendances sont compatibles Java 11 (Spring Boot 2.7.x, Gatling, JUnit 5, Micrometer, Actuator, etc.).
- Pas d'utilisation de modules Java 9+ (module-info.java), pour éviter les problèmes de migration et garder la compatibilité avec les outils classiques.

### 4. Build & exécution
- Utilisation de Maven avec configuration explicite du `sourceCompatibility` et `targetCompatibility` à 11 dans le `pom.xml`.
- Scripts d'automatisation compatibles Windows (PowerShell) pour faciliter l'intégration en environnement d'entreprise.
- Extraction automatisée des métriques via PowerShell après chaque test de charge.

### 5. Observabilité et monitoring
- Instrumentation des métriques métier et techniques avec Micrometer
- Exposition des métriques via Spring Boot Actuator (`/actuator/prometheus`)
- Intégration possible avec Prometheus et Grafana pour le suivi en production
- Extraction et affichage automatisés des métriques après chaque campagne de test

### 6. Limitations connues
- Pas d'accès aux API ou syntaxes des versions Java supérieures à 11.
- Si besoin de fonctionnalités avancées (virtual threads, records, etc.), une migration vers Java 17+ serait à prévoir.

## Conclusion
Le choix de Java 11 garantit la stabilité, la compatibilité et la portabilité du projet dans la plupart des environnements professionnels actuels, tout en permettant une base moderne pour l'asynchrone, les tests, l'observabilité et l'automatisation.

**Dernière mise à jour : juin 2025** 