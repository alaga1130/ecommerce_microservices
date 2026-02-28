package com.ecommerce.payment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payments")
public class Payment {

    @Id
    private String id;
    private String orderId;
    private double amount;
    private String status;
    private LocalDateTime createdAt;
    
    public Payment() {}

    public Payment(String orderId, double amount, String status) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(String id) { this.id = id; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}