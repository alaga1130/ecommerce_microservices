package com.ecommerce.order;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private String productId;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;

    public Order() {}

    public Order(String productId, int quantity, String status) {
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(String id) { this.id = id; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}