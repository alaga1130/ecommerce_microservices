package com.ecommerce.order;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

private ObservationRegistry observationRegistry = ObservationRegistry.create();
    @InjectMocks
    private OrderEventPublisher publisher;

    @Test
    void publish_shouldSendToKafka() {
        publisher.publish("order-events", "ORDER_CREATED:123:P1:2");

        verify(kafkaTemplate).send("order-events", "ORDER_CREATED:123:P1:2");
    }
}