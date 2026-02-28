package com.ecommerce.shipping;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShipmentRepository extends MongoRepository<Shipment, String> {
}