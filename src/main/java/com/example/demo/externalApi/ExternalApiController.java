package com.example.demo.externalApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ExternalApiController {
    private final ExternalApiService externalApiService = new ExternalApiService();

    @GetMapping("/external/calculatePrice")
    public Map<String, Object> calculatePrice(@RequestParam(defaultValue = "150") long delay) {
        double price = externalApiService.calculatePrice(delay);
        Map<String, Object> response = new HashMap<>();
        response.put("delay", delay);
        response.put("price", price);
        return response;
    }
} 