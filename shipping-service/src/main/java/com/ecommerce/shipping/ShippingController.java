package com.ecommerce.shipping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "Shipping Service is up";
    }

    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody ShipmentRequest request) {
        return ResponseEntity.ok(shippingService.createShipment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipment(@PathVariable String id) {
        return ResponseEntity.ok(shippingService.getShipment(id));
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return ResponseEntity.ok(shippingService.getAllShipments());
    }
}