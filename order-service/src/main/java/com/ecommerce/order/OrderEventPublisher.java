package com.ecommerce.order;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public OrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               ObservationRegistry observationRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.observationRegistry = observationRegistry;
    }

    @Retry(name = "kafkaPublisher")
    @CircuitBreaker(name = "kafkaPublisher", fallbackMethod = "publishFallback")
    public void publish(String topic, String event) {
        Observation.createNotStarted("order.kafka.producer", observationRegistry)
                .lowCardinalityKeyValue("topic", topic)
                .observe(() -> {
                    try {
                        kafkaTemplate.send(topic, event).get();
                        System.out.println("Order Service published event: " + event);
                    } catch (Exception ex) {
                        throw new RuntimeException("Kafka publish failed", ex);
                    }
                });
    }

    private void publishFallback(String topic, String event, Throwable ex) {
        System.err.println("Order Service FAILED to publish event after retries: " + event);
        ex.printStackTrace();
    }
}