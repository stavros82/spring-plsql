package com.example.demo.model;

import java.math.BigDecimal;

public record OrderResult(
        Long orderId,
        String status,
        BigDecimal total
) {}