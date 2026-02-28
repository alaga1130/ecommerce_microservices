package com.ecommerce.order;

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

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void ping_shouldReturnUpMessage() throws Exception {
        mockMvc.perform(get("/orders/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order Service is up"));
    }

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":\"P1\",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.productId").value("P1"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrder_shouldReturnOrder() throws Exception {
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");

        when(orderService.getOrder("123")).thenReturn(order);

        mockMvc.perform(get("/orders/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.productId").value("P1"));
    }

    @Test
    void getAllOrders_shouldReturnList() throws Exception {
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");

        when(orderService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123"));
    }

    @Test
    void cancelOrder_shouldReturnMessage() throws Exception {
        when(orderService.cancelOrder("123"))
                .thenReturn("Order cancellation requested for ID: 123");

        mockMvc.perform(delete("/orders/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order cancellation requested for ID: 123"));
    }

    @Test
    void getOrdersByStatus_shouldReturnFilteredList() throws Exception {
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");

        when(orderService.getOrdersByStatus("PENDING")).thenReturn(List.of(order));

        mockMvc.perform(get("/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}