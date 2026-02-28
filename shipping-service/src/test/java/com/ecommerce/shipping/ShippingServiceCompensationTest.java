package com.ecommerce.shipping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceCompensationTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShippingEventPublisher eventPublisher;

    @InjectMocks
    private ShippingService shippingService;

    @Test
    void markShippingFailed_shouldPublishFailureEvent() {
        shippingService.markShippingFailed("O1");

        verify(eventPublisher).publish("shipping-events", "SHIPPING_FAILED:O1");
    }
}