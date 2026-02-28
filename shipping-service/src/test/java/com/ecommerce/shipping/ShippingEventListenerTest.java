package com.ecommerce.shipping;

import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingEventListenerTest {

    @Mock
    private ShippingService shippingService;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private ShippingEventListener listener;

    @Test
    void listenShippingEvents_shouldCreateShipmentOnReadyToShip() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("shipping-events", 0, 0, null, "READY_TO_SHIP:O1:P1:2");

        listener.listenShippingEvents(record);

        verify(shippingService).createShipment(any(ShipmentRequest.class));
    }

    @Test
    void listenShippingEvents_shouldIgnoreUnknownEvent() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("shipping-events", 0, 0, null, "INVALID_EVENT");

        listener.listenShippingEvents(record);

        verifyNoInteractions(shippingService);
    }
}