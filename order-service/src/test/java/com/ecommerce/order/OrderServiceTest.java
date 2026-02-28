package com.ecommerce.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldSaveAndPublishEvent() {
        OrderRequest request = new OrderRequest();
        request.setProductId("P1");
        request.setQuantity(2);

        Order saved = new Order("P1", 2, "PENDING");
        saved.setId("123");

        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Order result = orderService.createOrder(request);

        assertEquals("123", result.getId());
        assertEquals("P1", result.getProductId());
        assertEquals(2, result.getQuantity());
        assertEquals("PENDING", result.getStatus());

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(
                eq("order-events"),
                eq("ORDER_CREATED:123:P1:2")
        );
    }

    @Test
    void getOrder_shouldReturnOrder() {
        Order order = new Order("P1", 2, "PENDING");
        order.setId("123");

        when(orderRepository.findById("123")).thenReturn(Optional.of(order));

        Order result = orderService.getOrder("123");

        assertEquals("123", result.getId());
        assertEquals("P1", result.getProductId());
    }

    @Test
    void getOrder_shouldThrowWhenNotFound() {
        when(orderRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrder("999"));
    }

    @Test
    void getAllOrders_shouldReturnList() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order("P1", 1, "PENDING")));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
    }

    @Test
    void cancelOrder_shouldPublishCancelEvent() {
        String msg = orderService.cancelOrder("123");

        assertEquals("Order cancellation requested for ID: 123", msg);
        verify(eventPublisher).publish("order-events", "ORDER_CANCELLED:123");
    }

    @Test
    void getOrdersByStatus_shouldDelegateToRepository() {
        when(orderRepository.findByStatus("PENDING"))
                .thenReturn(List.of(new Order("P1", 1, "PENDING")));

        List<Order> result = orderService.getOrdersByStatus("PENDING");

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void updateOrderStatus_shouldUpdateAndSave() {
        Order order = new Order("P1", 1, "PENDING");
        order.setId("123");

        when(orderRepository.findById("123")).thenReturn(Optional.of(order));

        orderService.updateOrderStatus("123", "SHIPPED");

        assertEquals("SHIPPED", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldThrowWhenNotFound() {
        when(orderRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("999", "SHIPPED"));
    }
}