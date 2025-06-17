# Gestion et configuration du pool de threads

## Objectifs de la configuration du pool

- **Réactivité** : Nous voulons que le traitement principal reste rapide et réactif.
- **Timeout raisonnable** : Nous ne voulons pas un timeout trop important pour éviter de bloquer les ressources inutilement.
- **Isolation** : Le pool de threads doit être isolé du thread principal pour ne pas ralentir le traitement principal.

## Configuration du pool

- **Taille du pool** : Le pool est configuré avec un nombre limité de threads (par exemple, 10 threads) pour éviter de surcharger le système.
- **File d'attente** : Une file d'attente est utilisée pour gérer les tâches qui ne peuvent pas être traitées immédiatement. La taille de cette file est limitée pour éviter une accumulation excessive de tâches.
- **Timeout** : Le timeout est fixé à 2000 ms pour les appels à l'API externe. Cela permet de libérer rapidement les ressources si l'API ne répond pas dans les temps.

## Impact sur le traitement principal

- **Non-bloquant** : En utilisant `CompletableFuture`, le traitement principal n'est pas bloqué par les appels à l'API externe. Cela permet de maintenir la réactivité de l'application.
- **Gestion des erreurs** : Si un appel à l'API externe dépasse le timeout, une erreur est retournée (HTTP 504), et le traitement principal peut continuer sans être affecté.

## Exemple de configuration

```java
ExecutorService executorService = Executors.newFixedThreadPool(10);
CompletableFuture<ApiResult> future = CompletableFuture.supplyAsync(() -> {
    // Appel à l'API externe
    return apiResult;
}, executorService);
```

## Conclusion

La configuration du pool de threads est cruciale pour maintenir la performance et la réactivité de l'application. En limitant le nombre de threads et en fixant un timeout raisonnable, nous nous assurons que le traitement principal n'est pas ralenti par les appels à l'API externe. 