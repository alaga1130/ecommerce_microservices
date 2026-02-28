package com.ecommerce.shipping;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public ShippingEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                  ObservationRegistry observationRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.observationRegistry = observationRegistry;
    }

    @Retry(name = "kafkaPublisher")
    @CircuitBreaker(name = "kafkaPublisher", fallbackMethod = "publishFallback")
    public void publish(String topic, String event) {
        Observation.createNotStarted("shipping.kafka.producer", observationRegistry)
                .lowCardinalityKeyValue("topic", topic)
                .observe(() -> {
                    try {
                        kafkaTemplate.send(topic, event).get();
                        System.out.println("Shipping Service published event: " + event);
                    } catch (Exception ex) {
                        throw new RuntimeException("Kafka publish failed", ex);
                    }
                });
    }

    private void publishFallback(String topic, String event, Throwable ex) {
        System.err.println("Shipping Service FAILED to publish event: " + event);
        ex.printStackTrace();
    }
}