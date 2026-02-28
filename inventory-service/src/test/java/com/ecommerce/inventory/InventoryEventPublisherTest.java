package com.ecommerce.inventory;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private InventoryEventPublisher publisher;

    @Test
    void publish_shouldSendEventToKafka() {
        publisher.publish("inventory-events", "INVENTORY_RESERVED:O1");

        verify(kafkaTemplate).send("inventory-events", "INVENTORY_RESERVED:O1");
    }
}