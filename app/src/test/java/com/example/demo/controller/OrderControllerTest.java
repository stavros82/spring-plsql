package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import com.example.demo.model.OrderResult;
import com.example.demo.repository.OrderPlsqlRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderPlsqlRepository orderPlsqlRepository;

    @Test
    @DisplayName("POST /api/orders - Returns 201 Created on valid request")
    void shouldCreateOrderSuccessfully() throws Exception {
        OrderRequest request = new OrderRequest(1001L, new BigDecimal("150.00"));
        OrderResult mockResult = new OrderResult(101L, "COMPLETED", new BigDecimal("150.00"));

        given(orderPlsqlRepository.processOrder(1001L, new BigDecimal("150.00")))
                .willReturn(mockResult);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(101))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.total").value(150.00));
    }

    @Test
    @DisplayName("POST /api/orders - Returns 400 Bad Request on validation failure")
    void shouldReturn400WhenValidationFails() throws Exception {
        OrderRequest invalidRequest = new OrderRequest(null, new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.totalAmount").exists());
    }
}