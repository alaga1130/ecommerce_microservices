package com.ecommerce.inventory;

public class InventoryRequest {

    private String productId;
    private int availableQuantity;

    public String getProductId() { return productId; }
    public int getAvailableQuantity() { return availableQuantity; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
}