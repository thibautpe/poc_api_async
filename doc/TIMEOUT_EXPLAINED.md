# Paramètres `delay` et `timeout` dans l'API `/handle`

## Schéma du flux

```mermaid
sequenceDiagram
    participant Client
    participant HandlerController
    participant AsyncExternalApiCaller
    participant ExternalApiController
    participant ExternalApiService

    Client->>HandlerController: GET /handle?delay=1500&apiTimeout=1000
    HandlerController->>AsyncExternalApiCaller: callExternalApiAsync(delay=1500, timeout=1000)
    AsyncExternalApiCaller->>ExternalApiController: GET /external/calculatePrice?delay=1500
    ExternalApiController->>ExternalApiService: calculatePrice(delay=1500)
    Note right of ExternalApiService: Thread.sleep(delay) simule le temps de réponse
    ExternalApiService-->>ExternalApiController: prix calculé (après delay ms)
    ExternalApiController-->>AsyncExternalApiCaller: JSON {price, delay}
    AsyncExternalApiCaller-->>HandlerController: CompletableFuture<ApiResult>
    HandlerController-->>Client: Réponse (succès ou timeout selon délai)
    Note over HandlerController: Si delay > timeout, le client reçoit un timeout
```

## Explications

- **delay** : temps d'attente simulé (en ms) par l'API externe.  
  Plus il est grand, plus la réponse de l'API externe est lente.
- **timeout** : durée maximale d'attente (en ms) côté `/handle` pour la réponse de l'API externe.  
  Si la réponse de l'API externe (donc le delay) dépasse ce timeout, le client reçoit une erreur de timeout.

**Cas d'usage :**
- `delay < timeout` → succès, le client reçoit un prix.
- `delay > timeout` → timeout, le client reçoit une erreur.

> **Voir [PATTERNS_ASYNC.md](PATTERNS_ASYNC.md) pour la gestion technique des timeouts et [USE_CASES_ASYNC_TIMEOUT.md](USE_CASES_ASYNC_TIMEOUT.md) pour les cas d'usage détaillés.**

**Dernière mise à jour : juin 2025** 