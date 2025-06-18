# README Gatling

Ce dossier contient les rapports de tests de charge générés par Gatling.

## gatlingloadtest1-20250618202631566 Note sur les erreurs 504 et les assertions

- Les erreurs HTTP 504 (Gateway Timeout) sont attendues dans ce test : elles surviennent lorsque le délai simulé de l'API externe dépasse le timeout configuré côté application.
- Les assertions de performance (max, percentiles) sont volontairement strictes pour illustrer le comportement en cas de surcharge ou de latence excessive.
- Le taux de succès/failure reflète la robustesse de la gestion des timeouts asynchrones dans l'application.

Pour toute analyse, ouvrez le rapport HTML généré (index.html) dans ce dossier. 