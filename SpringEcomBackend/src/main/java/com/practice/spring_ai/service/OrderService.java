package com.practice.spring_ai.service;

import com.practice.spring_ai.model.Order;
import com.practice.spring_ai.model.OrderItem;
import com.practice.spring_ai.model.Product;
import com.practice.spring_ai.model.dto.OrderItemRequest;
import com.practice.spring_ai.model.dto.OrderItemResponse;
import com.practice.spring_ai.model.dto.OrderRequest;
import com.practice.spring_ai.model.dto.OrderResponse;
import com.practice.spring_ai.repo.OrderRepo;
import com.practice.spring_ai.repo.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    public OrderResponse placeOrder(OrderRequest request) {
        log.info(
                "Placing order for customer='{}', email='{}', itemCount={}",
                request.customerName(),
                request.email(),
                request.items().size()
        );

        Order order = buildOrderHeader(request);
        List<OrderItem> orderItems = buildOrderItems(order, request.items());
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepo.save(order);
        log.info("Order persisted successfully: orderId='{}', dbId={}", savedOrder.getOrderId(), savedOrder.getId());

        List<OrderItemResponse> itemResponses = toItemResponses(order.getOrderItems());
        return toOrderResponse(savedOrder, itemResponses);
    }

    @Transactional
    public List<OrderResponse> getAllOrderResponses() {
        log.info("Fetching all orders");

        List<Order> orders = orderRepo.findAll();
        log.debug("Total orders fetched from database: {}", orders.size());

        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItemResponse> itemResponses = toItemResponses(order.getOrderItems());
            responses.add(toOrderResponse(order, itemResponses));
        }

        log.info("Prepared {} order response objects", responses.size());
        return responses;
    }

    private Order buildOrderHeader(OrderRequest request) {
        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        order.setOrderId(orderId);
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());

        return order;
    }

    private List<OrderItem> buildOrderItems(Order order, List<OrderItemRequest> itemRequests) {
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : itemRequests) {
            log.debug("Processing order item: productId={}, quantity={}", itemReq.productId(), itemReq.quantity());

            Product product = productRepo.findById(itemReq.productId())
                    .orElseThrow(() -> {
                        log.warn("Product not found while placing order: productId={}", itemReq.productId());
                        return new RuntimeException("Product not found");
                    });

            int updatedStock = product.getStockQuantity() - itemReq.quantity();
            log.debug(
                    "Updating stock for productId={}: {} -> {}",
                    product.getId(),
                    product.getStockQuantity(),
                    updatedStock
            );

            product.setStockQuantity(updatedStock);
            productRepo.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())))
                    .order(order)
                    .build();

            items.add(orderItem);
        }

        return items;
    }

    private List<OrderItemResponse> toItemResponses(List<OrderItem> orderItems) {
        List<OrderItemResponse> itemResponses = new ArrayList<>();

        for (OrderItem item : orderItems) {
            itemResponses.add(new OrderItemResponse(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getTotalPrice()
            ));
        }

        return itemResponses;
    }

    private OrderResponse toOrderResponse(Order order, List<OrderItemResponse> itemResponses) {
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerName(),
                order.getEmail(),
                order.getStatus(),
                order.getOrderDate(),
                itemResponses
        );
    }
}