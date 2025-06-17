package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void handlerEndpointShouldProcessRequest() {
        String response = this.restTemplate.getForObject(
                "http://localhost:" + port + "/handle?delay=100&apiTimeout=2000",
                String.class);

        // Vérifie que la réponse contient soit un prix, soit une erreur
        assertThat(response)
                .matches(r -> r.contains("completed with price:") || 
                            r.contains("ERREUR EXTERNAL_API"));
    }
} 