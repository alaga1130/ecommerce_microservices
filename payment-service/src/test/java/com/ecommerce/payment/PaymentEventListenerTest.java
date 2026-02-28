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
class PaymentEventListenerTest {

    @Mock
    private PaymentService paymentService;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private PaymentEventListener listener;

    @Test
    void listenOrderEvents_shouldTriggerPaymentCreation() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "ORDER_CREATED:O1:P1:2");

        listener.listenOrderEvents(record);

        verify(paymentService).createPaymentFromOrder("O1");
    }

    @Test
    void listenOrderEvents_shouldIgnoreInvalidEvent() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "INVALID_EVENT");

        listener.listenOrderEvents(record);

        verifyNoInteractions(paymentService);
    }

    @Test
    void listenOrderEvents_shouldIgnoreMalformedOrderCreated() {
        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("order-events", 0, 0, null, "ORDER_CREATED");

        listener.listenOrderEvents(record);

        verifyNoInteractions(paymentService);
    }
}