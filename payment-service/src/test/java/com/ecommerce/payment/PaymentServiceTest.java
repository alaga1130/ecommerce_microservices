package com.ecommerce.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

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
    void createPaymentFromOrder_shouldCreateAndPublishSuccess() {
        String orderId = "O1";

        Payment saved = new Payment();
        saved.setId(UUID.randomUUID().toString());
        saved.setOrderId(orderId);
        saved.setAmount(100);
        saved.setStatus("SUCCESS");

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        Payment result = paymentService.createPaymentFromOrder(orderId);

        assertEquals(orderId, result.getOrderId());
        assertEquals("SUCCESS", result.getStatus());

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(eventPublisher).publish("payment-events", "PAYMENT_SUCCESS:O1");
    }

    @Test
    void updatePaymentStatus_shouldUpdateExistingPayment() {
        Payment payment = new Payment();
        payment.setId("123");
        payment.setOrderId("O1");
        payment.setStatus("PENDING");

        when(paymentRepository.findByOrderId("O1")).thenReturn(List.of(payment));

        paymentService.updatePaymentStatus("O1", "FAILED");

        assertEquals("FAILED", payment.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void updatePaymentStatus_shouldThrowWhenNotFound() {
        when(paymentRepository.findByOrderId("O1")).thenReturn(List.of());

        assertThrows(RuntimeException.class,
                () -> paymentService.updatePaymentStatus("O1", "FAILED"));
    }

    @Test
    void getPaymentById_shouldReturnPayment() {
        Payment payment = new Payment();
        payment.setId("123");

        when(paymentRepository.findById("123")).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById("123");

        assertEquals("123", result.getId());
    }

    @Test
    void getPaymentById_shouldThrowWhenNotFound() {
        when(paymentRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> paymentService.getPaymentById("999"));
    }

    @Test
    void getAllPayments_shouldReturnList() {
        when(paymentRepository.findAll()).thenReturn(List.of(new Payment()));

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(1, result.size());
    }

    @Test
    void getPaymentsByStatus_shouldReturnFilteredList() {
        when(paymentRepository.findByStatus("SUCCESS"))
                .thenReturn(List.of(new Payment()));

        List<Payment> result = paymentService.getPaymentsByStatus("SUCCESS");

        assertEquals(1, result.size());
    }
}