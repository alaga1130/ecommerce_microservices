package com.ecommerce.order;

public class OrderRequest {

    private String productId;
    private int quantity;

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}