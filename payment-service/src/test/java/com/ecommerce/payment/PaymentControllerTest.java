package com.ecommerce.payment;

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

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void ping_shouldReturnUpMessage() throws Exception {
        mockMvc.perform(get("/payments/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment Service is up"));
    }

    @Test
    void createPayment_shouldReturnPayment() throws Exception {
        Payment payment = new Payment();
        payment.setId("123");
        payment.setOrderId("O1");
        payment.setStatus("SUCCESS");

        when(paymentService.createPaymentFromOrder("O1")).thenReturn(payment);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":\"O1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("O1"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getPayment_shouldReturnPayment() throws Exception {
        Payment payment = new Payment();
        payment.setId("123");

        when(paymentService.getPaymentById("123")).thenReturn(payment);

        mockMvc.perform(get("/payments/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"));
    }

    @Test
    void getAllPayments_shouldReturnList() throws Exception {
        Payment payment = new Payment();
        payment.setId("123");

        when(paymentService.getAllPayments()).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123"));
    }

    @Test
    void getPaymentsByStatus_shouldReturnFilteredList() throws Exception {
        Payment payment = new Payment();
        payment.setId("123");
        payment.setStatus("SUCCESS");

        when(paymentService.getPaymentsByStatus("SUCCESS")).thenReturn(List.of(payment));

        mockMvc.perform(get("/payments/status/SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));
    }
}