package com.example.demo;

import com.example.demo.api.AsyncExternalApiCaller;
import com.example.demo.api.AsyncExternalApiCaller.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HandlerController {
    private static final Logger logger = LoggerFactory.getLogger(HandlerController.class);
    private static final AtomicInteger requestCounter = new AtomicInteger(0);

    @Autowired
    private AsyncExternalApiCaller asyncExternalApiCaller;

    @GetMapping("/handle")
    public ResponseEntity<Map<String, Object>> handleRequest(
        @RequestParam(defaultValue = "100") long delay,
        @RequestParam(defaultValue = "2000") long apiTimeout
    ) {
        int requestId = requestCounter.incrementAndGet();
        logger.info("Request {} - Starting processing with delay: {}ms, apiTimeout: {}ms", String.format("%04d", requestId), delay, apiTimeout);

        CompletableFuture<ApiResult> future = asyncExternalApiCaller.callExternalApiAsync(delay, apiTimeout);
        ApiResult result;
        try {
            result = future.get(apiTimeout + 100, TimeUnit.MILLISECONDS); // marge de sécurité
        } catch (TimeoutException e) {
            asyncExternalApiCaller.logLateResponse(delay, apiTimeout);
            logger.error("Request {} - External API timeout after {}ms (requested delay: {}ms)", 
                String.format("%04d", requestId), apiTimeout, delay);
            Map<String, Object> response = new HashMap<>();
            response.put("requestId", String.format("%04d", requestId));
            response.put("error", "EXTERNAL_API_TIMEOUT");
            response.put("message", "Request timeout after " + apiTimeout + "ms (requested delay: " + delay + "ms)");
            response.put("status", "TIMEOUT");
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
        } catch (Exception e) {
            logger.error("Request {} - External API error: {}", String.format("%04d", requestId), e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("requestId", String.format("%04d", requestId));
            response.put("error", "EXTERNAL_API_ERROR");
            response.put("message", e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // Ajout d'un sleep de 2 secondes et d'une trace
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        logger.info("Request {} - Traitement pendant 2 secondes", String.format("%04d", requestId), delay, apiTimeout);

        if (!result.success && "EXTERNAL_API_TIMEOUT".equals(result.error)) {
            asyncExternalApiCaller.logLateResponse(delay, apiTimeout);
            logger.error("Request {} - External API timeout after {}ms (requested delay: {}ms)", 
                String.format("%04d", requestId), apiTimeout, delay);
            Map<String, Object> response = new HashMap<>();
            response.put("requestId", String.format("%04d", requestId));
            response.put("error", "EXTERNAL_API_TIMEOUT");
            response.put("message", "Request timeout after " + apiTimeout + "ms (requested delay: " + delay + "ms)");
            response.put("status", "TIMEOUT");
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
        }
        if (!result.success) {
            logger.error("Request {} - External API error: {}", String.format("%04d", requestId), result.error);
            Map<String, Object> response = new HashMap<>();
            response.put("requestId", String.format("%04d", requestId));
            response.put("error", "EXTERNAL_API_ERROR");
            response.put("message", result.error);
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        logger.info("Request {} - Processing completed with price: {} (API duration: {}ms)", String.format("%04d", requestId), result.price, result.durationMs);
        Map<String, Object> response = new HashMap<>();
        response.put("requestId", String.format("%04d", requestId));
        response.put("price", result.price);
        response.put("durationMs", result.durationMs);
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }
} 