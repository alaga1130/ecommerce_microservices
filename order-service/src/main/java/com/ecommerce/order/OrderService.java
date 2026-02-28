package com.ecommerce.order;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public Order getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public String cancelOrder(String id) {
        eventPublisher.publish("order-events", "ORDER_CANCELLED:" + id);
        return "Order cancellation requested for ID: " + id;
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public Order createOrder(OrderRequest request) {
        Order order = new Order(
                request.getProductId(),
                request.getQuantity(),
                "PENDING"
        );

        order = orderRepository.save(order);

        String event = String.format(
                "ORDER_CREATED:%s:%s:%d",
                order.getId(),
                order.getProductId(),
                order.getQuantity()
        );

        eventPublisher.publish("order-events", event);
        return order;
    }

    public void updateOrderStatus(String orderId, String status) {
        System.out.println(">>> Updating order " + orderId + " to status " + status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
        System.out.println(">>> Order updated successfully");
    }
}