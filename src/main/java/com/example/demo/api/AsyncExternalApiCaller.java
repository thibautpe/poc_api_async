package com.example.demo.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;

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
    private final MeterRegistry meterRegistry;
    private final Timer lateResponseTimer;
    private final Timer allCallsTimer;
    private final Timer successTimer;
    private final Timer errorTimer;
    private final Timer timeoutTimer;
    private final Counter totalCallsCounter;
    private final Counter http2xxCounter;
    private final Counter http4xxCounter;
    private final Counter http5xxCounter;
    private final Counter cancelledCounter;
    private final Timer cancelledTimer;

    @Autowired
    public AsyncExternalApiCaller(
        @Qualifier("asyncExecutor") ExecutorService executor,
        @Qualifier("timeoutScheduler") ScheduledExecutorService scheduler,
        MeterRegistry meterRegistry
    ) {
        this.executor = executor;
        this.scheduler = scheduler;
        this.meterRegistry = meterRegistry;
        this.lateResponseTimer = meterRegistry.timer("external_api.late_response.duration");
        this.allCallsTimer = meterRegistry.timer("external_api.all.duration");
        this.successTimer = meterRegistry.timer("external_api.success.duration");
        this.errorTimer = meterRegistry.timer("external_api.error.duration");
        this.timeoutTimer = meterRegistry.timer("external_api.timeout.duration");
        this.totalCallsCounter = meterRegistry.counter("external_api.total.count");
        this.http2xxCounter = meterRegistry.counter("external_api.http_2xx.count");
        this.http4xxCounter = meterRegistry.counter("external_api.http_4xx.count");
        this.http5xxCounter = meterRegistry.counter("external_api.http_5xx.count");
        this.cancelledCounter = meterRegistry.counter("external_api.cancelled.count");
        this.cancelledTimer = meterRegistry.timer("external_api.cancelled.duration");
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
                meterRegistry.counter("external_api.success.count").increment();
                allCallsTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                successTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                totalCallsCounter.increment();
                http2xxCounter.increment();
                return new ApiResult(true, price, null, duration, "SUCCESS");
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - start;
                meterRegistry.counter("external_api.error.count").increment();
                allCallsTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                errorTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                totalCallsCounter.increment();
                if (e instanceof org.springframework.web.client.HttpClientErrorException) {
                    http4xxCounter.increment();
                } else if (e instanceof org.springframework.web.client.HttpServerErrorException) {
                    http5xxCounter.increment();
                }
                logger.error("EXTERNAL_API_ERROR: {}", e.getMessage());
                return new ApiResult(false, null, "EXTERNAL_API_ERROR", duration, "ERROR");
            }
        }, executor);

        CompletableFuture<ApiResult> timeoutFuture = new CompletableFuture<>();
        scheduler.schedule(() -> {
            if (!future.isDone()) {
                timeoutCounter.incrementAndGet();
                meterRegistry.counter("external_api.timeout.count").increment();
                long duration = timeoutMs; // On ne connaît pas la durée réelle, on prend le timeout
                allCallsTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                timeoutTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                timeoutFuture.complete(new ApiResult(false, null, "EXTERNAL_API_TIMEOUT", timeoutMs, "TIMEOUT"));
                logger.error("EXTERNAL_API_TIMEOUT: Timeout after {} ms", timeoutMs);
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);

        return future.applyToEither(timeoutFuture, r -> r)
            .whenComplete((result, throwable) -> {
                if (future.isCancelled() || (throwable != null && throwable instanceof java.util.concurrent.CancellationException)) {
                    cancelledCounter.increment();
                    long duration = System.currentTimeMillis() - start;
                    cancelledTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                }
            });
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
                    meterRegistry.counter("external_api.late_response.count").increment();
                    lateResponseTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
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