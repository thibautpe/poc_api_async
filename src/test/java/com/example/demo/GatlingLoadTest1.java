package com.example.demo;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;
import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class GatlingLoadTest1 extends Simulation {
    private static final Random random = new Random();

    // HTTP Protocol Configuration
    private HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .userAgentHeader("Gatling/Performance Test")
        .maxConnectionsPerHost(10)
        .shareConnections();

    // Gaussian delay generator
    private int generateGaussianDelay(int min, int max, double mean, double stdDev) {
        double value;
        do {
            value = random.nextGaussian() * stdDev + mean;
        } while (value < min || value > max);
        return (int) value;
    }

    // Request definition
    private HttpRequestActionBuilder handleRequest = http("Handle Request")
        .get("/handle")
        .queryParam("delay", "#{delay}")
        .check(
            status().is(200).saveAs("status"),
            jsonPath("$.status").exists().saveAs("responseStatus"),
            jsonPath("$.error").optional().saveAs("errorType")
        )
        .checkIf((response, session) -> response.status().code() == 200).then(
            jsonPath("$.price").exists().saveAs("price"),
            jsonPath("$.durationMs").exists().saveAs("durationMs"),
            jsonPath("$.status").is("SUCCESS")
        )
        .checkIf((response, session) -> response.status().code() == 504).then(
            jsonPath("$.error").is("EXTERNAL_API_TIMEOUT"),
            jsonPath("$.message").exists(),
            jsonPath("$.status").is("TIMEOUT")
        );

    // Scenario definition
    private ScenarioBuilder scn = scenario("Handler Load Test")
        .exec(session -> {
            int delay = generateGaussianDelay(1500, 2500, 2000, 200);
            return session.set("delay", delay);
        })
        .exec(handleRequest)
        .pause(Duration.ofMillis(500));

    {
        // Simulation setup
        setUp(
            scn.injectOpen(
                rampUsers(3).during(5),
                constantUsersPerSec(1).during(20)
            )
        )
        .protocols(httpProtocol)
        .assertions(
            // Assertions globales
            global().responseTime().max().lt(4000),
            global().responseTime().percentile3().lt(3000),
            global().responseTime().percentile4().lt(3500),
            global().successfulRequests().percent().gt(40.0),
            global().failedRequests().percent().lt(60.0),
            
            // Assertions spécifiques pour les requêtes réussies
            details("Handle Request").responseTime().max().lt(4000),
            details("Handle Request").failedRequests().percent().lt(60.0),
            
            // Assertions pour les timeouts
            details("Handle Request").responseTime().percentile3().lt(3000),
            details("Handle Request").responseTime().percentile4().lt(3500),
            
            // Assertion sur le débit
            global().requestsPerSec().gt(0.5)
        );
    }
} 