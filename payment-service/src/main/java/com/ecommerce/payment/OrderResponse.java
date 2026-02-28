package com.ecommerce.payment;

public class OrderResponse {

    private String id;
    private String productId;
    private int quantity;
    private String status;

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStatus(String status) { this.status = status; }
}