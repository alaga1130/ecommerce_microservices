package com.ecommerce.payment;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private final PaymentService paymentService;
    private final ObservationRegistry observationRegistry;

    public PaymentEventListener(PaymentService paymentService,
                                ObservationRegistry observationRegistry) {
        this.paymentService = paymentService;
        this.observationRegistry = observationRegistry;
    }

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void listenOrderEvents(ConsumerRecord<String, String> record) {

        Observation.createNotStarted("kafka.consumer.payment.order", observationRegistry)
                .lowCardinalityKeyValue("topic", "order-events")
                .observe(() -> {

                    String event = record.value();
                    System.out.println("Payment Service received order event: " + event);

                    if (event.startsWith("ORDER_CREATED:")) {
                        String[] parts = event.split(":");
                        if (parts.length < 2) {
                            System.out.println("Invalid ORDER_CREATED event: " + event);
                            return;
                        }

                        String orderId = parts[1];
                        paymentService.createPaymentFromOrder(orderId);
                    }
                });
    }

    @KafkaListener(topics = "refund-events", groupId = "payment-group")
    public void listenRefundEvents(ConsumerRecord<String, String> record) {

        Observation.createNotStarted("kafka.consumer.payment.refund", observationRegistry)
                .lowCardinalityKeyValue("topic", "refund-events")
                .observe(() -> {

                    String event = record.value();
                    System.out.println("Payment Service received refund event: " + event);

                    if (event.startsWith("REFUND_REQUEST:")) {
                        String orderId = event.split(":")[1];
                        paymentService.refundPayment(orderId);
                    }
                });
    }
}