package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import com.example.demo.model.OrderResult;
import com.example.demo.repository.OrderPlsqlRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderPlsqlRepository orderPlsqlRepository;

    public OrderController(OrderPlsqlRepository orderPlsqlRepository) {
        this.orderPlsqlRepository = orderPlsqlRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResult> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResult result = orderPlsqlRepository.processOrder(
                request.customerId(),
                request.totalAmount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}