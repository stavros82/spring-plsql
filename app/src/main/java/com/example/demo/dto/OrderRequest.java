package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRequest(
        @NotNull(message = "Customer ID is mandatory")
        Long customerId,

        @NotNull(message = "Total amount is mandatory")
        @Positive(message = "Total amount must be greater than zero")
        BigDecimal totalAmount
) {}