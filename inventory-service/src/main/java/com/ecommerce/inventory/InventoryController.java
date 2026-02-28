package com.ecommerce.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "Inventory Service is up";
    }

    @PostMapping
    public ResponseEntity<Inventory> addInventory(@RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.addInventory(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventory(@PathVariable String id) {
        return ResponseEntity.ok(inventoryService.getInventory(id));
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }
}