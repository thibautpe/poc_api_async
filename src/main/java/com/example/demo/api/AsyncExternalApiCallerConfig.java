package com.example.demo.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AsyncExternalApiCallerConfig {
    @Bean(name = "asyncExecutor")
    public ExecutorService asyncExecutor(@Value("${async.pool.size:4}") int poolSize) {
        return Executors.newFixedThreadPool(poolSize);
    }

    @Bean(name = "timeoutScheduler")
    public ScheduledExecutorService timeoutScheduler(@Value("${timeout.scheduler.size:2}") int schedulerSize) {
        return Executors.newScheduledThreadPool(schedulerSize);
    }
} 