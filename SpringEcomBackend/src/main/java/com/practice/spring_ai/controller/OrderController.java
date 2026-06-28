package com.practice.spring_ai.controller;

import com.practice.spring_ai.model.dto.OrderRequest;
import com.practice.spring_ai.model.dto.OrderResponse;
import com.practice.spring_ai.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Received request to place an order");
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        log.info("Order placed successfully with id={}", orderResponse.orderId());
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("Received request to fetch all orders");
        List<OrderResponse> orderResponseList = orderService.getAllOrderResponses();
        log.debug("Fetched {} orders", orderResponseList.size());
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }
}
