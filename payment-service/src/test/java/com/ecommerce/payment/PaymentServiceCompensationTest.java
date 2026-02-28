package com.ecommerce.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceCompensationTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @Mock
    private org.springframework.web.client.RestTemplate restTemplate;

    @Mock
    private io.micrometer.tracing.Tracer tracer;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void refundPayment_shouldUpdateStatusAndPublishEvent() {
        Payment payment = new Payment();
        payment.setOrderId("O1");
        payment.setStatus("SUCCESS");

        when(paymentRepository.findByOrderId("O1")).thenReturn(List.of(payment));

        paymentService.refundPayment("O1");

        assertEquals("REFUNDED", payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(eventPublisher).publish("payment-events", "REFUND_COMPLETED:O1");
    }

    @Test
    void refundPayment_shouldThrowWhenPaymentNotFound() {
        when(paymentRepository.findByOrderId("O1")).thenReturn(List.of());

        assertThrows(RuntimeException.class,
                () -> paymentService.refundPayment("O1"));
    }
}