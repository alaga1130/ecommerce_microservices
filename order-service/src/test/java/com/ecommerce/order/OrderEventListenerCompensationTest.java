package com.ecommerce.order;

import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerCompensationTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderEventPublisher eventPublisher;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private OrderEventListener listener;

    @Test
    void paymentFailed_shouldCancelOrder() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("payment-events", 0, 0, null, "PAYMENT_FAILED:O1");

        listener.handlePaymentEvents(record);

        verify(orderService).updateOrderStatus("O1", "CANCELLED");
        verify(eventPublisher, never()).publish(any(), any());
    }

    @Test
    void inventoryFailed_shouldCancelOrder_andTriggerRefund() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("inventory-events", 0, 0, null, "INVENTORY_FAILED:O1");

        listener.handleInventoryEvents(record);

        verify(orderService).updateOrderStatus("O1", "CANCELLED");
        verify(eventPublisher).publish("payment-events", "REFUND_REQUEST:O1");
    }

    @Test
    void shippingFailed_shouldMarkOrderFailed() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("shipping-events", 0, 0, null, "SHIPPING_FAILED:O1");

        listener.handleShippingEvents(record);

        verify(orderService).updateOrderStatus("O1", "FAILED");
    }
}