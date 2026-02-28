package com.ecommerce.inventory;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {

    private final InventoryService inventoryService;
    private final ObservationRegistry observationRegistry;

    public InventoryEventListener(InventoryService inventoryService,
                                  ObservationRegistry observationRegistry) {
        this.inventoryService = inventoryService;
        this.observationRegistry = observationRegistry;
    }

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void listenOrderEvents(ConsumerRecord<String, String> record) {

        Observation.createNotStarted("inventory.kafka.consumer", observationRegistry)
                .lowCardinalityKeyValue("topic", "order-events")
                .observe(() -> {

                    String event = record.value();
                    System.out.println("Inventory Service received event: " + event);

                    if (!event.startsWith("ORDER_CREATED:")) {
                        System.out.println("Ignoring unknown event: " + event);
                        return;
                    }

                    // Expected format: ORDER_CREATED:orderId:productId:quantity
                    String[] parts = event.split(":");
                    if (parts.length != 4) {
                        System.out.println("Invalid ORDER_CREATED event format: " + event);
                        return;
                    }

                    String orderId = parts[1];
                    String productId = parts[2];
                    int quantity;

                    try {
                        quantity = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity in event: " + event);
                        return;
                    }

                    inventoryService.updateStock(orderId, productId, quantity);
                });
    }
}