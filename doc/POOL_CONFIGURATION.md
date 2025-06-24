# Gestion et configuration du pool de threads

## Objectifs de la configuration du pool

- **Réactivité** : Nous voulons que le traitement principal reste rapide et réactif.
- **Timeout raisonnable** : Nous ne voulons pas un timeout trop important pour éviter de bloquer les ressources inutilement.
- **Isolation** : Le pool de threads doit être isolé du thread principal pour ne pas ralentir le traitement principal.

## Configuration (Spring Bean)

Les pools de threads sont créés une seule fois au démarrage de l'application et injectés via Spring. Cela évite toute fuite de threads et permet de mutualiser les ressources entre tous les appels asynchrones.

- **ExecutorService partagé** : utilisé pour l'exécution asynchrone des appels à l'API externe.
- **ScheduledExecutorService partagé** : utilisé pour la gestion des timeouts.

### Exemple de configuration Spring

```java
@Configuration
public class AsyncExternalApiCallerConfig {
    @Bean(name = "asyncExecutor")
    public ExecutorService asyncExecutor() {
        return Executors.newFixedThreadPool(4); // Taille configurable
    }

    @Bean(name = "timeoutScheduler")
    public ScheduledExecutorService timeoutScheduler() {
        return Executors.newScheduledThreadPool(2); // Taille configurable
    }
}
```

Dans la classe de service :

```java
@Autowired
public AsyncExternalApiCaller(
    @Qualifier("asyncExecutor") ExecutorService executor,
    @Qualifier("timeoutScheduler") ScheduledExecutorService scheduler
) {
    this.executor = executor;
    this.scheduler = scheduler;
}
```

## Impact sur le traitement principal

- **Non-bloquant** : En utilisant `CompletableFuture`, le traitement principal n'est pas bloqué par les appels à l'API externe. Cela permet de maintenir la réactivité de l'application.
- **Gestion des erreurs** : Si un appel à l'API externe dépasse le timeout, une erreur est retournée (HTTP 504), et le traitement principal peut continuer sans être affecté.
- **Pas de fuite de threads** : Les pools sont mutualisés et gérés par Spring, ce qui évite la création excessive de threads.

## Conclusion

La configuration du pool de threads via des beans Spring permet de garantir la performance, la réactivité et la robustesse de l'application. En limitant le nombre de threads et en fixant un timeout raisonnable, nous nous assurons que le traitement principal n'est pas ralenti par les appels à l'API externe, tout en évitant les fuites de ressources.

> **Note importante :**
> Les pools de threads (ExecutorService et ScheduledExecutorService) sont mutualisés et gérés par Spring. **Il ne faut pas les arrêter (shutdown) après chaque usage**. Un shutdown prématuré empêcherait le traitement des futures tâches et pourrait provoquer des erreurs en charge réelle. Le cycle de vie de ces pools est géré par le conteneur Spring et ils sont arrêtés proprement à l'arrêt de l'application.

**Dernière mise à jour : juin 2025** 