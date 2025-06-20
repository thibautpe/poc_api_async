# Diagrammes d'architecture du POC

## 1. Diagramme de classes détaillé

```mermaid
classDiagram
    class DemoApplication {
        +main(String[] args)
    }
    class HandlerController {
        -AsyncExternalApiCaller asyncExternalApiCaller
        +handleRequest(long delay, long apiTimeout) : ResponseEntity<Map<String, Object>>
        <<REST Controller>>
    }
    class AsyncExternalApiCaller {
        -ExecutorService executor
        -ScheduledExecutorService scheduler
        -MeterRegistry meterRegistry
        -Timer lateResponseTimer
        -Timer allCallsTimer
        -Timer successTimer
        -Timer errorTimer
        -Timer timeoutTimer
        -Counter totalCallsCounter
        -Counter http2xxCounter
        -Counter http4xxCounter
        -Counter http5xxCounter
        -Counter cancelledCounter
        -Timer cancelledTimer
        -AtomicInteger timeoutCounter
        -AtomicInteger lateResponseCounter
        -RestTemplate restTemplate
        -String externalApiUrl
        +callExternalApiAsync(long delay, long timeoutMs) : CompletableFuture<ApiResult>
        +logLateResponse(long delay, long timeoutMs)
        +getTimeoutCount() : int
        +getLateResponseCount() : int
        +setExternalApiUrl(String)
    }
    class ApiResult {
        +boolean success
        +Double price
        +String error
        +long durationMs
        +String status
    }
    class AsyncExternalApiCallerConfig {
        +asyncExecutor(int poolSize) : ExecutorService
        +timeoutScheduler(int schedulerSize) : ScheduledExecutorService
        <<@Configuration>>
    }
    class ExternalApiController {
        -ExternalApiService externalApiService
        +calculatePrice(long delay) : Map<String, Object>
        <<REST Controller>>
    }
    class ExternalApiService {
        +calculatePrice(long delayMs) : double
    }
    class GatlingLoadTest1 {
        -HttpProtocolBuilder httpProtocol
        -HttpRequestActionBuilder handleRequest
        -ScenarioBuilder scn
        +setUp()
        <<Gatling Simulation>>
    }
    DemoApplication --> HandlerController
    HandlerController --> AsyncExternalApiCaller
    AsyncExternalApiCaller --> ApiResult
    HandlerController --> ExternalApiController : (via HTTP)
    ExternalApiController --> ExternalApiService
    AsyncExternalApiCallerConfig ..> AsyncExternalApiCaller : (inject pools)
    GatlingLoadTest1 --> HandlerController : (via HTTP)
```

### Description des classes principales

- **DemoApplication** : Point d'entrée de l'application Spring Boot.
- **HandlerController** : Contrôleur REST qui gère les requêtes `/handle` et utilise `AsyncExternalApiCaller` pour appeler l'API externe de façon asynchrone.
- **AsyncExternalApiCaller** : Service qui encapsule l'appel asynchrone à l'API externe, la gestion des timeouts, des pools de threads et l'instrumentation des métriques (Micrometer).
- **ApiResult** : Classe représentant le résultat de l'appel à l'API externe (succès, erreur, prix, durée, statut).
- **AsyncExternalApiCallerConfig** : Configuration Spring pour injecter les pools de threads utilisés par `AsyncExternalApiCaller`.
- **ExternalApiController** : Contrôleur REST simulant une API externe lente.
- **ExternalApiService** : Service de calcul simulé pour l'API externe.
- **GatlingLoadTest1** : Simulation Gatling qui génère la charge sur `/handle` et vérifie les réponses et les SLA.

---

## 2. Diagramme de séquence (flux d'une requête asynchrone)

```mermaid
sequenceDiagram
    participant Client
    participant HandlerController
    participant AsyncExternalApiCaller
    participant ExternalApiController
    participant ExternalApiService
    participant Prometheus

    Client->>HandlerController: GET /handle?delay=xxx&apiTimeout=yyy
    HandlerController->>AsyncExternalApiCaller: callExternalApiAsync(delay, timeout)
    AsyncExternalApiCaller->>ExternalApiController: HTTP GET /external/calculatePrice?delay=xxx
    ExternalApiController->>ExternalApiService: calculatePrice(delay)
    ExternalApiService-->>ExternalApiController: prix calculé
    ExternalApiController-->>AsyncExternalApiCaller: JSON {price, delay}
    AsyncExternalApiCaller-->>HandlerController: CompletableFuture<ApiResult>
    HandlerController-->>Client: HTTP 200/504 (selon délai)
    AsyncExternalApiCaller-->>Prometheus: +1 métrique (succès, timeout, late_response, etc.)
    note over AsyncExternalApiCaller,Prometheus: Toutes les métriques sont exposées via /actuator/prometheus
```

### Explications du flux
- Le client appelle `/handle` avec un délai et un timeout.
- Le contrôleur délègue à `AsyncExternalApiCaller` qui lance l'appel asynchrone.
- L'appel HTTP est fait vers l'API externe simulée.
- Le service simule un délai et retourne un prix.
- Selon le délai, le contrôleur retourne un succès ou un timeout (504).
- Les métriques sont incrémentées et exposées pour Prometheus.

---

## 3. Notes complémentaires

- Les pools de threads sont injectés et mutualisés via Spring (voir `AsyncExternalApiCallerConfig`).
- L'observabilité est assurée par Micrometer, Prometheus, et l'extraction automatisée des métriques après chaque test de charge.
- Le test de charge Gatling simule des scénarios variés et vérifie les SLA sur `/handle`.
- Le découplage entre contrôleur, service asynchrone et API externe permet de tester facilement la résilience et la scalabilité.

---

## 4. Diagramme de séquence : scénario de timeout

```mermaid
sequenceDiagram
    participant Client
    participant HandlerController
    participant AsyncExternalApiCaller
    participant ExternalApiController
    participant ExternalApiService
    participant Prometheus

    Client->>HandlerController: GET /handle?delay=3000&apiTimeout=2000
    HandlerController->>AsyncExternalApiCaller: callExternalApiAsync(3000, 2000)
    AsyncExternalApiCaller->>ExternalApiController: HTTP GET /external/calculatePrice?delay=3000
    ExternalApiController->>ExternalApiService: calculatePrice(3000)
    Note right of AsyncExternalApiCaller: Timer de timeout (2000ms)
    HandlerController-->>Client: HTTP 504 (timeout)
    AsyncExternalApiCaller-->>Prometheus: +1 external_api.timeout.count
    AsyncExternalApiCaller-->>Prometheus: +1 external_api.timeout.duration
    Note over HandlerController,Client: Le contrôleur répond avant la fin réelle de l'appel externe
    ExternalApiService-->>ExternalApiController: prix calculé (après 3000ms)
    ExternalApiController-->>AsyncExternalApiCaller: JSON {price, delay}
    AsyncExternalApiCaller-->>Prometheus: +1 external_api.late_response.count
    AsyncExternalApiCaller-->>Prometheus: +1 external_api.late_response.duration
    AsyncExternalApiCaller-->>HandlerController: logLateResponse()
    Note over AsyncExternalApiCaller: Log "late response" et métriques
```

### Explications du scénario timeout
- Le client demande un délai supérieur au timeout autorisé.
- Le contrôleur délègue à `AsyncExternalApiCaller` qui lance l'appel asynchrone et le timer de timeout.
- Le timeout est atteint avant la réponse de l'API externe : le contrôleur répond en 504 (Gateway Timeout).
- Les métriques de timeout sont incrémentées.
- Quand la réponse externe arrive finalement, une métrique "late response" est incrémentée et un log est généré.
- Ce scénario permet de monitorer la robustesse et la réactivité du système face aux lenteurs externes. 