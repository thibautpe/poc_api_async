package com.example.demo.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AsyncExternalApiCaller {
    private static final Logger logger = LoggerFactory.getLogger(AsyncExternalApiCaller.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicInteger timeoutCounter = new AtomicInteger(0);
    private final AtomicInteger lateResponseCounter = new AtomicInteger(0);
    @Value("${external.api.url}")
    private String externalApiUrl;

    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;

    @Autowired
    public AsyncExternalApiCaller(
        @Qualifier("asyncExecutor") ExecutorService executor,
        @Qualifier("timeoutScheduler") ScheduledExecutorService scheduler
    ) {
        this.executor = executor;
        this.scheduler = scheduler;
    }

    public static class ApiResult {
        public final boolean success;
        public final Double price;
        public final String error;
        public final long durationMs;
        public final String status;

        public ApiResult(boolean success, Double price, String error, long durationMs, String status) {
            this.success = success;
            this.price = price;
            this.error = error;
            this.durationMs = durationMs;
            this.status = status;
        }
    }

    public CompletableFuture<ApiResult> callExternalApiAsync(long delay, long timeoutMs) {
        long start = System.currentTimeMillis();
        CompletableFuture<ApiResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.getForObject(
                        externalApiUrl + "?delay=" + delay, Map.class);
                Double price = response != null ? ((Number) response.get("price")).doubleValue() : null;
                long duration = System.currentTimeMillis() - start;
                return new ApiResult(true, price, null, duration, "SUCCESS");
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - start;
                logger.error("EXTERNAL_API_ERROR: {}", e.getMessage());
                return new ApiResult(false, null, "EXTERNAL_API_ERROR", duration, "ERROR");
            }
        }, executor);

        CompletableFuture<ApiResult> timeoutFuture = new CompletableFuture<>();
        scheduler.schedule(() -> {
            if (!future.isDone()) {
                timeoutCounter.incrementAndGet();
                timeoutFuture.complete(new ApiResult(false, null, "EXTERNAL_API_TIMEOUT", timeoutMs, "TIMEOUT"));
                logger.error("EXTERNAL_API_TIMEOUT: Timeout after {} ms", timeoutMs);
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);

        return future.applyToEither(timeoutFuture, r -> r);
    }

    public void logLateResponse(long delay, long timeoutMs) {
        executor.submit(() -> {
            long start = System.currentTimeMillis();
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.getForObject(
                        externalApiUrl + "?delay=" + delay, Map.class);
                Double price = response != null ? ((Number) response.get("price")).doubleValue() : null;
                long duration = System.currentTimeMillis() - start;
                if (duration > timeoutMs) {
                    lateResponseCounter.incrementAndGet();
                    logger.error("EXTERNAL_API_LATE: Réponse arrivée après le timeout ({} ms), durée réelle: {} ms, prix: {}", timeoutMs, duration, price);
                }
            } catch (Exception e) {
                logger.error("EXTERNAL_API_LATE_ERROR: {}", e.getMessage());
            }
        });
    }

    public int getTimeoutCount() {
        return timeoutCounter.get();
    }

    public int getLateResponseCount() {
        return lateResponseCounter.get();
    }

    public void setExternalApiUrl(String externalApiUrl) {
        this.externalApiUrl = externalApiUrl;
    }
} 