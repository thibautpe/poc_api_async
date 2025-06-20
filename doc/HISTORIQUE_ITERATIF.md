# Historique itératif du projet : démarche, choix et évolutions

Ce document retrace les principales étapes, itérations et décisions qui ont jalonné la construction et l'amélioration du POC API asynchrone. Il met en avant l'approche incrémentale, collaborative et orientée "vibe coding" adoptée tout au long du projet.

---

## 1. Démarrage et objectifs

- **Objectif initial** : Démontrer la gestion asynchrone et des timeouts dans une API Java Spring Boot, avec simulation d'API externe lente et tests de charge Gatling.
- **Première version** : Mise en place d'une structure simple, endpoints principaux, logique asynchrone, premiers tests unitaires et de charge.

---

## 2. Amélioration de la documentation technique

- **Ajout et structuration de la documentation** :
  - Création de nombreux fichiers dans `doc/` pour détailler l'architecture, les patterns asynchrones, la configuration des pools, l'observabilité, les cas d'usage, etc.
  - Vérification de l'exhaustivité des liens dans le README.
- **Correction des diagrammes Mermaid** :
  - Détection d'un problème d'affichage sur GitHub (classe imbriquée non supportée).
  - Correction de la syntaxe Mermaid pour rendre les diagrammes compatibles et lisibles partout.

---

## 3. Approche qualité et exhaustivité

- **Vérification de la couverture documentaire** :
  - Audit des fichiers présents dans `doc/` vs ceux référencés dans le README.
  - Ajout d'une section dédiée dans le README pour lister et décrire chaque document technique.
- **Vérification de la structure du code** :
  - Analyse de l'arborescence réelle du code source (Java, tests, ressources).
  - Mise à jour du schéma de structure dans le README pour refléter fidèlement tous les fichiers et dossiers (y compris les sous-packages, tests Gatling, etc.).

---

## 4. Enrichissement et professionnalisation du README

- **Réorganisation complète du README** :
  - Ajout d'un "Quick Start" pour faciliter l'onboarding.
  - Séparation claire des sections : configuration, scripts d'automatisation, sécurité, scalabilité, glossaire, etc.
  - Ajout de conseils pour la personnalisation, la sécurité (avertissement POC), l'extensibilité.
- **Ajout d'un glossaire** pour clarifier les termes techniques.

---

## 5. Démarche itérative et "vibe coding"

- **Travail en cycles courts** :
  - Chaque amélioration ou correction a été discutée, validée, puis intégrée immédiatement.
  - Les retours utilisateurs (problèmes d'affichage, d'exhaustivité, de clarté) ont été traités en temps réel.
- **Transparence et traçabilité** :
  - Toutes les modifications sont justifiées et documentées (dans le chat, les commits, et maintenant ce fichier).
  - L'approche "vibe coding" s'est traduite par une adaptation continue aux besoins, une écoute active et une volonté de rendre le projet pédagogique et réutilisable.

---

## 6. Résultat

- Un POC robuste, bien documenté, facile à prendre en main et à étendre.
- Une documentation exhaustive, à jour, et structurée pour accompagner aussi bien la découverte que l'industrialisation.
- Un exemple concret de démarche itérative, collaborative et orientée qualité.

---

*Ce document illustre comment l'amélioration continue, la transparence et l'agilité peuvent transformer un simple POC en une base solide pour des développements futurs.* 