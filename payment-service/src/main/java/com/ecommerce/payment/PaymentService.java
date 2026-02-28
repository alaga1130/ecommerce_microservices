package com.ecommerce.payment;

import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final PaymentEventPublisher eventPublisher;
    private final Tracer tracer;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentEventPublisher eventPublisher,
                          RestTemplate restTemplate,
                          Tracer tracer) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
        this.restTemplate = restTemplate;
        this.tracer = tracer;
    }

    public Payment createPaymentFromOrder(String orderId) {

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setOrderId(orderId);
        payment.setAmount(100);
        payment.setStatus("PENDING");

        paymentRepository.save(payment);

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        String event = "PAYMENT_SUCCESS:" + orderId;
        eventPublisher.publish("payment-events", event);
        System.out.println("Payment successful for order: " + orderId);

        return payment;
    }

    public void updatePaymentStatus(String orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    public void refundPayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("REFUNDED");
        paymentRepository.save(payment);

        String event = "REFUND_COMPLETED:" + orderId;
        eventPublisher.publish("payment-events", event);
        System.out.println("Refund completed for order: " + orderId);
    }

    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }
}