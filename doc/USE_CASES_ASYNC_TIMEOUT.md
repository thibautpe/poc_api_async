# Cas d'usage asynchrone & gestion du timeout

## 1. Réponse de l'API externe **avant** `API_TIMEOUT_MS`
- Le contrôleur `/handle` attend la réponse de l'API externe via un `CompletableFuture`.
- Si l'API externe répond **avant** le délai (ex: 1500 ms pour un timeout à 2000 ms) :
    - Le contrôleur récupère le résultat (prix, durée, etc.).
    - Il retourne une réponse HTTP 200 avec le JSON de succès :
      ```json
      {
        "requestId": "0001",
        "price": 1234.56,
        "durationMs": 1500,
        "status": "SUCCESS"
      }
      ```
    - Le test Gatling compte cette requête comme un succès (OK).

## 2. Réponse de l'API externe **après** `API_TIMEOUT_MS`
- Si l'API externe répond **après** le délai (ex: 2300 ms pour un timeout à 2000 ms) :
    - Le contrôleur ne récupère pas le résultat à temps.
    - Il retourne une réponse HTTP 504 (Gateway Timeout) avec un JSON d'erreur :
      ```json
      {
        "requestId": "0002",
        "error": "EXTERNAL_API_TIMEOUT",
        "message": "Request timeout after 2000ms (requested delay: 2300ms)",
        "status": "TIMEOUT"
      }
      ```
    - Le test Gatling compte cette requête comme une erreur (KO).
    - Quand la réponse de l'API externe arrive finalement, elle est logguée côté serveur comme "late" (trop tard), mais n'est pas renvoyée au client.

## 3. Cas de **forte concurrence** (ex: 50 appels parallèles)
- Chaque appel à `/handle` crée un `CompletableFuture` qui s'exécute dans un pool de threads.
- **Pool de threads** :
    - Si le pool est dimensionné pour supporter 50 threads, tous les appels sont traités en parallèle.
    - Si le pool est plus petit (ex: 10 threads), les appels au-delà de la capacité attendent qu'un thread se libère (file d'attente interne).
    - Si la file d'attente du pool est saturée, de nouveaux appels peuvent être rejetés ou attendre indéfiniment (selon la configuration du pool).
- **Conséquences** :
    - Si tous les threads sont occupés et que les délais sont longs, certains appels risquent d'atteindre le timeout avant d'être traités.
    - Le serveur peut logguer des erreurs de timeout ou de saturation du pool.
    - Les requêtes qui dépassent le timeout reçoivent un HTTP 504, même si l'API externe finit par répondre.

## 4. Cas limites et robustesse
- **API externe très lente** : la majorité des requêtes recevront un 504, le serveur loggue de nombreux "late".
- **Surcharge extrême** : si le nombre d'appels dépasse largement la capacité du pool, la latence augmente, le taux d'erreur (504) grimpe, et le serveur peut devenir instable si la mémoire est saturée.
- **API externe rapide** : toutes les requêtes sont traitées avant le timeout, le taux de succès est maximal.

## Résumé
- Le système est robuste tant que la charge reste dans la capacité du pool et que l'API externe répond dans les temps.
- Les timeouts sont gérés proprement (HTTP 504), les réponses tardives sont logguées mais non transmises au client.
- En cas de surcharge ou d'API lente, le taux d'erreur augmente, ce qui est visible dans les rapports Gatling. 