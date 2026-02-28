package com.ecommerce.payment;

import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerCompensationTest {

    @Mock
    private PaymentService paymentService;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private PaymentEventListener listener;

    @Test
    void refundRequest_shouldTriggerRefund() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "REFUND_REQUEST:O1");

        listener.listenOrderEvents(record);

        verify(paymentService).refundPayment("O1");
    }

    @Test
    void invalidEvent_shouldNotTriggerRefund() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "INVALID");

        listener.listenOrderEvents(record);

        verifyNoInteractions(paymentService);
    }
}