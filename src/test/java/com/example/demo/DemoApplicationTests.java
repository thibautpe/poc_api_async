package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import com.example.demo.api.AsyncExternalApiCaller;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AsyncExternalApiCaller asyncExternalApiCaller;

    @BeforeEach
    public void setUp() {
        // Initialisation dynamique de l'URL avec le port correct
        asyncExternalApiCaller.setExternalApiUrl("http://localhost:" + port + "/external/calculatePrice");
        System.out.println("Port utilisé pour le test (BeforeEach) = " + port);
    }

    @ParameterizedTest(name = "delay={0}, timeout={1}")
    @CsvSource({
        "100,2000",   // delay < timeout : succès attendu
        "2500,1000"   // delay > timeout : timeout attendu
    })
    public void handlerEndpointShouldProcessRequest(long delay, long apiTimeout) {
        String response = this.restTemplate.getForObject(
                "http://localhost:" + port + "/handle?delay=" + delay + "&apiTimeout=" + apiTimeout,
                String.class);

        // Vérifie que la réponse est cohérente avec le scénario
        if (delay < apiTimeout) {
            assertThat(response)
                .contains("\"price\"")
                .contains("SUCCESS");
        } else {
            assertThat(response)
                .contains("EXTERNAL_API_TIMEOUT")
                .contains("TIMEOUT");
        }
    }

    /**
     * Note :
     * Lorsqu'un timeout survient, un appel asynchrone à logLateResponse est lancé pour simuler la récupération tardive du résultat externe.
     * Si le serveur embarqué de test est arrêté avant que ce thread n'ait terminé, une erreur EXTERNAL_API_LATE_ERROR (Connection refused) peut apparaître dans les logs.
     * Cela n'est pas bloquant et n'affecte pas le résultat des tests : c'est un effet normal du test d'intégration asynchrone.
     */
} 