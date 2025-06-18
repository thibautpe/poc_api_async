package com.example.demo.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AsyncExternalApiCallerConfig {
    @Bean(name = "asyncExecutor")
    public ExecutorService asyncExecutor() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean(name = "timeoutScheduler")
    public ScheduledExecutorService timeoutScheduler() {
        return Executors.newScheduledThreadPool(2);
    }
} 