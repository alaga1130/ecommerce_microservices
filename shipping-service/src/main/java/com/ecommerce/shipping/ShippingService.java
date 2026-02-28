package com.ecommerce.shipping;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final ShippingEventPublisher eventPublisher;

    public ShippingService(ShipmentRepository shipmentRepository,
                           ShippingEventPublisher eventPublisher) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
    }

    public Shipment createShipment(ShipmentRequest request) {
        Shipment shipment = new Shipment();
        shipment.setId(UUID.randomUUID().toString());
        shipment.setOrderId(request.getOrderId());
        shipment.setProductId(request.getProductId());
        shipment.setQuantity(request.getQuantity());
        shipment.setStatus("SHIPPED");

        shipmentRepository.save(shipment);

        String event = "SHIPPED:" + request.getOrderId();
        eventPublisher.publish("shipping-events", event);

        return shipment;
    }

    // Example: call this when external shipping fails
    public void markShippingFailed(String orderId) {
        // optional: update shipment status if exists
        shipmentRepository.findAll().stream()
                .filter(s -> orderId.equals(s.getOrderId()))
                .findFirst()
                .ifPresent(s -> {
                    s.setStatus("FAILED");
                    shipmentRepository.save(s);
                });

        String event = "SHIPPING_FAILED:" + orderId;
        eventPublisher.publish("shipping-events", event);
    }

    public Shipment getShipment(String id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }
}