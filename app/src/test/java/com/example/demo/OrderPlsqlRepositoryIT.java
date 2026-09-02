package com.example.demo;

import com.example.demo.model.OrderResult;
import com.example.demo.repository.OrderPlsqlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class OrderPlsqlRepositoryIT {

    @Container
    static OracleContainer oracleContainer = new OracleContainer(
            DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart")
    )
            .withDatabaseName("FREEPDB1")
            .withUsername("SYS")
            .withPassword("OraclePassword123");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracleContainer::getJdbcUrl);
        registry.add("spring.datasource.username", () -> "sys as sysdba");
        registry.add("spring.datasource.password", oracleContainer::getPassword);
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    @Autowired
    private OrderPlsqlRepository orderPlsqlRepository;

    @Test
    @DisplayName("Should execute PL/SQL package and return mapped ref cursor result for valid order")
    void shouldProcessValidOrderSuccessfully() {
        Long customerId = 1001L;
        BigDecimal amount = new BigDecimal("250.50");

        OrderResult result = orderPlsqlRepository.processOrder(customerId, amount);

        assertThat(result).isNotNull();
        assertThat(result.orderId()).isNotNull();
        assertThat(result.status()).isEqualTo("COMPLETED");
        assertThat(result.total()).isEqualByComparingTo(amount);
    }

    @Test
    @DisplayName("Should capture custom ORA-20001 exception when negative total amount is passed")
    void shouldThrowExceptionWhenAmountIsInvalid() {
        Long customerId = 1001L;
        BigDecimal invalidAmount = new BigDecimal("-50.00");

        assertThatThrownBy(() -> orderPlsqlRepository.processOrder(customerId, invalidAmount))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("ORA-20001");
    }
}