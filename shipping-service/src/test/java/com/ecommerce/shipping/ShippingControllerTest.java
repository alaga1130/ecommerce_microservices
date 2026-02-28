package com.ecommerce.shipping;

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

@WebMvcTest(ShippingController.class)
class ShippingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShippingService shippingService;

    @Test
    void ping_shouldReturnUpMessage() throws Exception {
        mockMvc.perform(get("/shipping/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("Shipping Service is up"));
    }

    @Test
    void createShipment_shouldReturnShipment() throws Exception {
        Shipment shipment = new Shipment();
        shipment.setId("123");
        shipment.setOrderId("O1");
        shipment.setProductId("P1");
        shipment.setQuantity(2);
        shipment.setStatus("SHIPPED");

        when(shippingService.createShipment(any(ShipmentRequest.class))).thenReturn(shipment);

        mockMvc.perform(post("/shipping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":\"O1\",\"productId\":\"P1\",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("O1"))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void getShipment_shouldReturnShipment() throws Exception {
        Shipment shipment = new Shipment();
        shipment.setId("123");

        when(shippingService.getShipment("123")).thenReturn(shipment);

        mockMvc.perform(get("/shipping/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));
    }

    @Test
    void getAllShipments_shouldReturnList() throws Exception {
        Shipment shipment = new Shipment();
        shipment.setId("123");

        when(shippingService.getAllShipments()).thenReturn(List.of(shipment));

        mockMvc.perform(get("/shipping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123"));
    }
}