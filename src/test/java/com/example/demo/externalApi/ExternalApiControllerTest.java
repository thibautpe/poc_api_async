package com.example.demo.externalApi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExternalApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void calculatePrice_shouldReturnPriceInRange() throws Exception {
        long delay = 200;
        mockMvc.perform(get("/external/calculatePrice").param("delay", String.valueOf(delay)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delay").value(delay))
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.price").value(org.hamcrest.Matchers.greaterThanOrEqualTo(100.0)))
                .andExpect(jsonPath("$.price").value(org.hamcrest.Matchers.lessThanOrEqualTo(3000.0)));
    }

    @Test
    void calculatePrice_shouldUseDefaultDelay() throws Exception {
        mockMvc.perform(get("/external/calculatePrice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delay").value(150))
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.price").value(org.hamcrest.Matchers.greaterThanOrEqualTo(100.0)))
                .andExpect(jsonPath("$.price").value(org.hamcrest.Matchers.lessThanOrEqualTo(3000.0)));
    }
} 