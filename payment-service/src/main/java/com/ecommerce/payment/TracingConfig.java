package com.ecommerce.payment;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class TracingConfig {

    @Bean
    public KafkaTemplate<String, String> tracedKafkaTemplate(
            ProducerFactory<String, String> pf,
            ObservationRegistry registry) {

        KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);
        template.setObservationEnabled(true);   // ⭐ REQUIRED FOR ZIPKIN
        return template;
    }
}