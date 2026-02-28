package com.ecommerce.inventory;

import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEventListenerTest {

    @Mock
    private InventoryService inventoryService;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private InventoryEventListener listener;

    @Test
    void listenOrderEvents_shouldProcessValidOrderCreatedEvent() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "ORDER_CREATED:O1:P1:5");

        listener.listenOrderEvents(record);

        verify(inventoryService).updateStock("O1", "P1", 5);
    }

    @Test
    void listenOrderEvents_shouldIgnoreUnknownEvent() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "SOMETHING_ELSE");

        listener.listenOrderEvents(record);

        verifyNoInteractions(inventoryService);
    }

    @Test
    void listenOrderEvents_shouldIgnoreInvalidFormat() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "ORDER_CREATED:BAD");

        listener.listenOrderEvents(record);

        verifyNoInteractions(inventoryService);
    }

    @Test
    void listenOrderEvents_shouldIgnoreInvalidQuantity() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "ORDER_CREATED:O1:P1:XYZ");

        listener.listenOrderEvents(record);

        verifyNoInteractions(inventoryService);
    }
}