package com.ecommerce.payment;

import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObservationRegistry observationRegistry = ObservationRegistry.create();

    @InjectMocks
    private PaymentEventPublisher publisher;

    @Test
    void publish_shouldSendEventToKafka() {
        publisher.publish("payment-events", "PAYMENT_SUCCESS:O1");

        verify(kafkaTemplate).send("payment-events", "PAYMENT_SUCCESS:O1");
    }
}