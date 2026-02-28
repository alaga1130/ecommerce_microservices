package com.ecommerce.inventory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventPublisher eventPublisher;

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryEventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }

    // ADD or INCREASE STOCK
    public Inventory addInventory(InventoryRequest request) {

        return inventoryRepository.findByProductId(request.getProductId())
                .map(existing -> {
                    // Increase stock
                    existing.setAvailableQuantity(existing.getAvailableQuantity() + request.getAvailableQuantity());
                    existing.setStatus("AVAILABLE");
                    existing.setUpdatedAt(LocalDateTime.now());
                    return inventoryRepository.save(existing);
                })
                .orElseGet(() -> {
                    // Create new product
                    Inventory inventory = new Inventory(
                            request.getProductId(),
                            request.getAvailableQuantity(),
                            "AVAILABLE"
                    );
                    inventory.setId(UUID.randomUUID().toString());
                    return inventoryRepository.save(inventory);
                });
    }

    public Inventory getInventory(String id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    // REDUCE STOCK FOR ORDER
    public void updateStock(String orderId, String productId, int quantity) {

        Inventory item = inventoryRepository.findByProductId(productId)
                .orElse(null);

        if (item == null) {
            System.out.println("No inventory found for productId: " + productId);
            eventPublisher.publish("inventory-events", "INVENTORY_FAILED:" + orderId);
            return;
        }

        if (item.getAvailableQuantity() >= quantity) {
            item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
            item.setStatus("RESERVED");
            item.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(item);

            eventPublisher.publish("inventory-events", "INVENTORY_RESERVED:" + orderId);

            System.out.println("Inventory reserved for order: " + orderId +
                    ", productId=" + productId + ", quantity=" + quantity);

        } else {
            System.out.println("Insufficient stock for productId: " + productId +
                    ", required=" + quantity + ", available=" + item.getAvailableQuantity());

            eventPublisher.publish("inventory-events", "INVENTORY_FAILED:" + orderId);
        }
    }
}