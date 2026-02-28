package com.ecommerce.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceCompensationTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryEventPublisher eventPublisher;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void updateStock_noInventory_shouldPublishFailed() {
        when(inventoryRepository.findByProductId("P1")).thenReturn(Optional.empty());

        inventoryService.updateStock("O1", "P1", 5);

        verify(eventPublisher).publish("inventory-events", "INVENTORY_FAILED:O1");
    }

    @Test
    void updateStock_insufficientStock_shouldPublishFailed() {
        Inventory inv = new Inventory("P1", 2, "AVAILABLE");
        inv.setId("123");

        when(inventoryRepository.findByProductId("P1")).thenReturn(Optional.of(inv));

        inventoryService.updateStock("O1", "P1", 5);

        verify(eventPublisher).publish("inventory-events", "INVENTORY_FAILED:O1");
    }
}