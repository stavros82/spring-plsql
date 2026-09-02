package com.example.demo;

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
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class OrderPlsqlFailureIT {

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
    @DisplayName("Should raise DataAccessException wrapping ORA-20001 when total amount is zero")
    void shouldFailWhenAmountIsZero() {
        Long customerId = 1001L;
        BigDecimal zeroAmount = BigDecimal.ZERO;

        assertThatThrownBy(() -> orderPlsqlRepository.processOrder(customerId, zeroAmount))
                .isInstanceOf(DataAccessException.class)
                .hasRootCauseInstanceOf(SQLException.class)
                .hasMessageContaining("ORA-20001");
    }

    @Test
    @DisplayName("Should raise DataAccessException wrapping ORA-20001 when total amount is negative")
    void shouldFailWhenAmountIsNegative() {
        Long customerId = 1001L;
        BigDecimal negativeAmount = new BigDecimal("-100.00");

        assertThatThrownBy(() -> orderPlsqlRepository.processOrder(customerId, negativeAmount))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("ORA-20001");
    }
}