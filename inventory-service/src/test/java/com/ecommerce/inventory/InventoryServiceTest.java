package com.ecommerce.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryEventPublisher eventPublisher;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void addInventory_shouldCreateNewInventory() {
        InventoryRequest req = new InventoryRequest();
        req.setProductId("P1");
        req.setAvailableQuantity(10);

        when(inventoryRepository.findByProductId("P1")).thenReturn(Optional.empty());

        Inventory saved = new Inventory("P1", 10, "AVAILABLE");
        saved.setId(UUID.randomUUID().toString());

        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        Inventory result = inventoryService.addInventory(req);

        assertEquals("P1", result.getProductId());
        assertEquals(10, result.getAvailableQuantity());
        assertEquals("AVAILABLE", result.getStatus());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void addInventory_shouldIncreaseExistingStock() {
        Inventory existing = new Inventory("P1", 5, "AVAILABLE");
        existing.setId("123");

        InventoryRequest req = new InventoryRequest();
        req.setProductId("P1");
        req.setAvailableQuantity(5);

        when(inventoryRepository.findByProductId("P1")).thenReturn(Optional.of(existing));
        when(inventoryRepository.save(existing)).thenReturn(existing);

        Inventory result = inventoryService.addInventory(req);

        assertEquals(10, result.getAvailableQuantity());
        verify(inventoryRepository).save(existing);
    }

    @Test
    void getInventory_shouldReturnItem() {
        Inventory item = new Inventory("P1", 10, "AVAILABLE");
        item.setId("123");

        when(inventoryRepository.findById("123")).thenReturn(Optional.of(item));

        Inventory result = inventoryService.getInventory("123");

        assertEquals("P1", result.getProductId());
    }

    @Test
    void getInventory_shouldThrowWhenNotFound() {
        when(inventoryRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventoryService.getInventory("999"));
    }

    @Test
    void updateStock_shouldPublishReservedWhenStockAvailable() {
        Inventory item = new Inventory("P1", 10, "AVAILABLE");
        item.setId("123");

        when(inventoryRepository.findByProductId("P1")).thenReturn(Optional.of(item));

        inventoryService.updateStock("O1", "P1", 5);

        assertEquals(5, item.getAvailableQuantity());
        verify(inventoryRepository).save(item);
        verify(eventPublisher).publish("inventory-events", "INVENTORY_RESERVED:O1");
    }

    @Test
    void updateStock_shouldPublishFailedWhenNoInventory() {
        when(inventoryRepository.findByProductId("P1")).thenReturn(Optional.empty());

        inventoryService.updateStock("O1", "P1", 5);

        verify(eventPublisher).publish("inventory-events", "INVENTORY_FAILED:O1");
    }

    @Test
    void updateStock_shouldPublishFailedWhenInsufficientStock() {
        Inventory item = new Inventory("P1", 3, "AVAILABLE");
        item.setId("123");

        when(inventoryRepository.findByProductId("P1")).thenReturn(Optional.of(item));

        inventoryService.updateStock("O1", "P1", 5);

        verify(eventPublisher).publish("inventory-events", "INVENTORY_FAILED:O1");
    }
}