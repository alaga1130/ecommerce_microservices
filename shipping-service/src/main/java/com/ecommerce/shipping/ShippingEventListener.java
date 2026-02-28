package com.ecommerce.shipping;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventListener {

    private final ShippingService shippingService;
    private final ObservationRegistry observationRegistry;

    public ShippingEventListener(ShippingService shippingService,
                                 ObservationRegistry observationRegistry) {
        this.shippingService = shippingService;
        this.observationRegistry = observationRegistry;
    }

    @KafkaListener(topics = "shipping-events", groupId = "shipping-group")
    public void listenShippingEvents(ConsumerRecord<String, String> record) {

        Observation.createNotStarted("kafka.consumer.shipping", observationRegistry)
                .lowCardinalityKeyValue("topic", "shipping-events")
                .observe(() -> {

                    String event = record.value();
                    System.out.println("Shipping Service received event: " + event);

                    if (event.startsWith("READY_TO_SHIP:")) {
                        String[] parts = event.split(":");

                        String orderId = parts[1];
                        String productId = parts[2];
                        int quantity = Integer.parseInt(parts[3]);

                        ShipmentRequest req = new ShipmentRequest();
                        req.setOrderId(orderId);
                        req.setProductId(productId);
                        req.setQuantity(quantity);

                        shippingService.createShipment(req);
                    }
                });
    }
}