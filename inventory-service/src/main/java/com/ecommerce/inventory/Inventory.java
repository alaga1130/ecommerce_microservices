package com.ecommerce.inventory;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "inventory")
public class Inventory {

    @Id
    private String id;

    @Indexed(unique = true)
    private String productId;

    private int availableQuantity;
    private String status;
    private LocalDateTime updatedAt;

    public Inventory() {}

    public Inventory(String productId, int availableQuantity, String status) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id) { this.id = id; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public void setStatus(String status) { this.status = status; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}