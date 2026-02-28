package com.ecommerce.payment;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByStatus(String status);

    List<Payment> findByOrderId(String orderId);
}