package com.ecommerce.gateway;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TracingConfig {

    @Bean
    public WebClient webClient(ObservationRegistry registry) {
        return WebClient.builder()
                .observationRegistry(registry)   // ⭐ REQUIRED FOR ZIPKIN TRACING
                .build();
    }
}