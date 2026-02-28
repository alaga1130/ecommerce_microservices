package com.ecommerce.shipping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShippingEventPublisher eventPublisher;

    @InjectMocks
    private ShippingService shippingService;

    @Test
    void createShipment_shouldSaveAndPublishShippedEvent() {
        ShipmentRequest req = new ShipmentRequest();
        req.setOrderId("O1");
        req.setProductId("P1");
        req.setQuantity(2);

        Shipment saved = new Shipment();
        saved.setId(UUID.randomUUID().toString());
        saved.setOrderId("O1");
        saved.setProductId("P1");
        saved.setQuantity(2);
        saved.setStatus("SHIPPED");

        when(shipmentRepository.save(any(Shipment.class))).thenReturn(saved);

        Shipment result = shippingService.createShipment(req);

        assertEquals("O1", result.getOrderId());
        assertEquals("SHIPPED", result.getStatus());

        verify(shipmentRepository).save(any(Shipment.class));
        verify(eventPublisher).publish("shipping-events", "SHIPPED:O1");
    }

    @Test
    void getShipment_shouldReturnShipment() {
        Shipment shipment = new Shipment();
        shipment.setId("123");

        when(shipmentRepository.findById("123")).thenReturn(Optional.of(shipment));

        Shipment result = shippingService.getShipment("123");

        assertEquals("123", result.getId());
    }

    @Test
    void getShipment_shouldThrowWhenNotFound() {
        when(shipmentRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> shippingService.getShipment("999"));
    }

    @Test
    void getAllShipments_shouldReturnList() {
        when(shipmentRepository.findAll()).thenReturn(List.of(new Shipment()));

        List<Shipment> result = shippingService.getAllShipments();

        assertEquals(1, result.size());
    }
}