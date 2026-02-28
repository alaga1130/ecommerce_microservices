package com.ecommerce.shipping;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShippingEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private ShippingEventPublisher publisher;

    @Test
    void publish_shouldSendEventToKafka() {
        publisher.publish("shipping-events", "SHIPPED:O1");

        verify(kafkaTemplate).send("shipping-events", "SHIPPED:O1");
    }
}