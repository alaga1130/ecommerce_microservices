package com.ecommerce.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void ping_shouldReturnUpMessage() throws Exception {
        mockMvc.perform(get("/inventory/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventory Service is up"));
    }

    @Test
    void addInventory_shouldReturnCreatedInventory() throws Exception {
        Inventory inv = new Inventory("P1", 10, "AVAILABLE");
        inv.setId("123");

        when(inventoryService.addInventory(any(InventoryRequest.class))).thenReturn(inv);

        mockMvc.perform(post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":\"P1\",\"availableQuantity\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("P1"))
                .andExpect(jsonPath("$.availableQuantity").value(10));
    }

    @Test
    void getInventory_shouldReturnItem() throws Exception {
        Inventory inv = new Inventory("P1", 10, "AVAILABLE");
        inv.setId("123");

        when(inventoryService.getInventory("123")).thenReturn(inv);

        mockMvc.perform(get("/inventory/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));
    }

    @Test
    void getAllInventory_shouldReturnList() throws Exception {
        Inventory inv = new Inventory("P1", 10, "AVAILABLE");
        inv.setId("123");

        when(inventoryService.getAllInventory()).thenReturn(List.of(inv));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123"));
    }
}