package com.ecommerce.order;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderEventListener {

    private final OrderService orderService;
    private final OrderEventPublisher eventPublisher;
    private final ObservationRegistry observationRegistry;

    // Track partial success for READY_TO_SHIP
    private final Map<String, Boolean> paymentOk = new ConcurrentHashMap<>();
    private final Map<String, Boolean> inventoryOk = new ConcurrentHashMap<>();

    public OrderEventListener(OrderService orderService,
                              OrderEventPublisher eventPublisher,
                              ObservationRegistry observationRegistry) {
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
        this.observationRegistry = observationRegistry;
    }

    // ---------------------------------------------------------
    // PAYMENT EVENTS
    // ---------------------------------------------------------
    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void handlePaymentEvents(ConsumerRecord<String, String> record) {
        Observation.createNotStarted("kafka.consumer.payment", observationRegistry)
                .lowCardinalityKeyValue("topic", "payment-events")
                .observe(() -> {

                    String event = record.value();
                    System.out.println("Order Service received payment event: " + event);

                    if (event.startsWith("PAYMENT_SUCCESS:")) {
                        String orderId = event.split(":")[1];

                        Order order = orderService.getOrder(orderId);

                        // Prevent overwriting CANCELLED or REFUNDED
                        if ("CANCELLED".equals(order.getStatus()) || "REFUNDED".equals(order.getStatus())) {
                            System.out.println("Ignoring PAYMENT_SUCCESS for cancelled/refunded order: " + orderId);
                            return;
                        }

                        // Prevent overwriting READY_TO_SHIP or SHIPPED
                        if ("READY_TO_SHIP".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus())) {
                            System.out.println("Ignoring PAYMENT_SUCCESS because order already ready/shipped: " + orderId);
                            return;
                        }

                        orderService.updateOrderStatus(orderId, "PAYMENT_SUCCESS");
                        paymentOk.put(orderId, true);
                        checkAndPublishReadyToShip(orderId);
                    }

                    if (event.startsWith("PAYMENT_FAILED:")) {
                        String orderId = event.split(":")[1];

                        Order order = orderService.getOrder(orderId);

                        // If already shipped or ready, ignore
                        if ("READY_TO_SHIP".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus())) {
                            System.out.println("Ignoring PAYMENT_FAILED because order already ready/shipped: " + orderId);
                            return;
                        }

                        orderService.updateOrderStatus(orderId, "CANCELLED");
                        paymentOk.remove(orderId);
                        inventoryOk.remove(orderId);
                    }

                    if (event.startsWith("REFUND_COMPLETED:")) {
                        String orderId = event.split(":")[1];
                        orderService.updateOrderStatus(orderId, "REFUNDED");
                    }
                });
    }

    // ---------------------------------------------------------
    // INVENTORY EVENTS
    // ---------------------------------------------------------
    @KafkaListener(topics = "inventory-events", groupId = "order-group")
    public void handleInventoryEvents(ConsumerRecord<String, String> record) {
        Observation.createNotStarted("kafka.consumer.inventory", observationRegistry)
                .lowCardinalityKeyValue("topic", "inventory-events")
                .observe(() -> {

                    String event = record.value();
                    System.out.println("Order Service received inventory event: " + event);

                    if (event.startsWith("INVENTORY_RESERVED:")) {
                        String orderId = event.split(":")[1];

                        Order order = orderService.getOrder(orderId);

                        // Prevent overwriting CANCELLED or REFUNDED
                        if ("CANCELLED".equals(order.getStatus()) || "REFUNDED".equals(order.getStatus())) {
                            System.out.println("Ignoring INVENTORY_RESERVED for cancelled/refunded order: " + orderId);
                            return;
                        }

                        // Prevent overwriting READY_TO_SHIP or SHIPPED
                        if ("READY_TO_SHIP".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus())) {
                            System.out.println("Ignoring INVENTORY_RESERVED because order already ready/shipped: " + orderId);
                            return;
                        }

                        orderService.updateOrderStatus(orderId, "INVENTORY_RESERVED");
                        inventoryOk.put(orderId, true);
                        checkAndPublishReadyToShip(orderId);
                    }

                    if (event.startsWith("INVENTORY_FAILED:")) {
                        String orderId = event.split(":")[1];

                        Order order = orderService.getOrder(orderId);

                        // If already shipped or ready, ignore
                        if ("READY_TO_SHIP".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus())) {
                            System.out.println("Ignoring INVENTORY_FAILED because order already ready/shipped: " + orderId);
                            return;
                        }

                        orderService.updateOrderStatus(orderId, "CANCELLED");
                        paymentOk.remove(orderId);
                        inventoryOk.remove(orderId);

                        // Trigger refund
                        String refundEvent = "REFUND_REQUEST:" + orderId;
                        eventPublisher.publish("refund-events", refundEvent);
                    }
                });
    }

    // ---------------------------------------------------------
    // SHIPPING EVENTS
    // ---------------------------------------------------------
    @KafkaListener(topics = "shipping-events", groupId = "order-group")
    public void handleShippingEvents(ConsumerRecord<String, String> record) {
        Observation.createNotStarted("kafka.consumer.shipping", observationRegistry)
                .lowCardinalityKeyValue("topic", "shipping-events")
                .observe(() -> {

                    String event = record.value();
                    System.out.println("Order Service received shipping event: " + event);

                    if (event.startsWith("SHIPPED:")) {
                        String orderId = event.split(":")[1];

                        Order order = orderService.getOrder(orderId);

                        if ("CANCELLED".equals(order.getStatus()) || "REFUNDED".equals(order.getStatus())) {
                            System.out.println("Ignoring SHIPPED for cancelled/refunded order: " + orderId);
                            return;
                        }

                        orderService.updateOrderStatus(orderId, "SHIPPED");
                    }

                    if (event.startsWith("SHIPPING_FAILED:")) {
                        String orderId = event.split(":")[1];

                        Order order = orderService.getOrder(orderId);

                        if ("CANCELLED".equals(order.getStatus()) || "REFUNDED".equals(order.getStatus())) {
                            System.out.println("Ignoring SHIPPING_FAILED for cancelled/refunded order: " + orderId);
                            return;
                        }

                        orderService.updateOrderStatus(orderId, "FAILED");
                    }
                });
    }

    // ---------------------------------------------------------
    // READY_TO_SHIP LOGIC
    // ---------------------------------------------------------
    private void checkAndPublishReadyToShip(String orderId) {
        boolean payOk = paymentOk.getOrDefault(orderId, false);
        boolean invOk = inventoryOk.getOrDefault(orderId, false);

        if (payOk && invOk) {
            Order order = orderService.getOrder(orderId);

            if ("CANCELLED".equals(order.getStatus()) || "REFUNDED".equals(order.getStatus())) {
                System.out.println("Not publishing READY_TO_SHIP for cancelled/refunded order: " + orderId);
                paymentOk.remove(orderId);
                inventoryOk.remove(orderId);
                return;
            }

            String event = "READY_TO_SHIP:" +
                    order.getId() + ":" +
                    order.getProductId() + ":" +
                    order.getQuantity();

            eventPublisher.publish("shipping-events", event);
            orderService.updateOrderStatus(orderId, "READY_TO_SHIP");

            paymentOk.remove(orderId);
            inventoryOk.remove(orderId);
        }
    }
}