package com.ecommerce.shipping;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "shipments")
public class Shipment {

    @Id
    private String id;

    private String orderId;
    private String productId;   
    private int quantity;

    private String status;
    private LocalDateTime shippedAt;

    public Shipment() {}

    public Shipment(String orderId, String productId, int quantity, String status) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.shippedAt = LocalDateTime.now();
    }


    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }     
    public int getQuantity() { return quantity; }          
    public String getStatus() { return status; }
    public LocalDateTime getShippedAt() { return shippedAt; }

  
    public void setId(String id) { this.id = id; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setProductId(String productId) { this.productId = productId; }   // NEW
    public void setQuantity(int quantity) { this.quantity = quantity; }          // NEW
    public void setStatus(String status) { this.status = status; }
    public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }
}