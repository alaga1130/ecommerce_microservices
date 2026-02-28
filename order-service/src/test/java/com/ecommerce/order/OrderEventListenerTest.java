package com.ecommerce.order;

import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderEventPublisher eventPublisher;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private OrderEventListener listener;

    // ---------------------------------------------------------
    // PAYMENT EVENTS
    // ---------------------------------------------------------

    @Test
    void handlePaymentEvents_shouldHandlePaymentSuccess() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("payment-events", 0, 0, null, "PAYMENT_SUCCESS:123");

        // lenient because READY_TO_SHIP may not trigger getOrder()
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");
        lenient().when(orderService.getOrder("123")).thenReturn(order);

        listener.handlePaymentEvents(record);

        verify(orderService).updateOrderStatus("123", "PAYMENT_SUCCESS");
        verify(eventPublisher, never()).publish(any(), any());
    }

    @Test
    void handlePaymentEvents_shouldHandlePaymentFailed() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("payment-events", 0, 0, null, "PAYMENT_FAILED:123");

        listener.handlePaymentEvents(record);

        verify(orderService).updateOrderStatus("123", "CANCELLED");
        verify(eventPublisher, never()).publish(any(), any());
    }

    // ---------------------------------------------------------
    // INVENTORY EVENTS
    // ---------------------------------------------------------

    @Test
    void handleInventoryEvents_shouldHandleInventoryReserved() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("inventory-events", 0, 0, null, "INVENTORY_RESERVED:123");

        // lenient because READY_TO_SHIP may not trigger getOrder()
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");
        lenient().when(orderService.getOrder("123")).thenReturn(order);

        listener.handleInventoryEvents(record);

        verify(orderService).updateOrderStatus("123", "INVENTORY_RESERVED");
        verify(eventPublisher, never()).publish(any(), any());
    }

    @Test
    void handleInventoryEvents_shouldHandleInventoryFailed() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("inventory-events", 0, 0, null, "INVENTORY_FAILED:123");

        listener.handleInventoryEvents(record);

        verify(orderService).updateOrderStatus("123", "CANCELLED");
        verify(eventPublisher).publish("payment-events", "REFUND_REQUEST:123");
    }

    // ---------------------------------------------------------
    // READY_TO_SHIP (requires both payment + inventory success)
    // ---------------------------------------------------------

    @Test
    void readyToShip_shouldBePublishedAfterBothEvents() {
        // 1. Payment success
        listener.handlePaymentEvents(
                new ConsumerRecord<>("payment-events", 0, 0, null, "PAYMENT_SUCCESS:123")
        );

        // 2. Inventory reserved
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");

        when(orderService.getOrder("123")).thenReturn(order);

        listener.handleInventoryEvents(
                new ConsumerRecord<>("inventory-events", 0, 0, null, "INVENTORY_RESERVED:123")
        );

        verify(eventPublisher).publish(eq("shipping-events"), contains("READY_TO_SHIP:123"));
        verify(orderService).updateOrderStatus("123", "READY_TO_SHIP");
    }

    // ---------------------------------------------------------
    // SHIPPING EVENTS
    // ---------------------------------------------------------

    @Test
    void handleShippingEvents_shouldHandleShipped() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("shipping-events", 0, 0, null, "SHIPPED:123");

        listener.handleShippingEvents(record);

        verify(orderService).updateOrderStatus("123", "SHIPPED");
    }

    @Test
    void handleShippingEvents_shouldHandleShippingFailed() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("shipping-events", 0, 0, null, "SHIPPING_FAILED:123");

        listener.handleShippingEvents(record);

        verify(orderService).updateOrderStatus("123", "FAILED");
    }
}