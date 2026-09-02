package com.example.demo.repository;

import com.example.demo.model.OrderResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class OrderPlsqlRepository {

    private final SimpleJdbcCall simpleJdbcCall;

    public OrderPlsqlRepository(JdbcTemplate jdbcTemplate) {
        this.simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("ORDER_PROCESSOR")
                .withProcedureName("PROCESS_AND_FETCH")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("P_CUSTOMER_ID", Types.NUMERIC),
                        new SqlParameter("P_TOTAL_AMOUNT", Types.NUMERIC),
                        new SqlOutParameter("P_ORDER_CURSOR", Types.REF_CURSOR, (rs, rowNum) -> new OrderResult(
                                rs.getLong("ORDER_ID"),
                                rs.getString("STATUS"),
                                // Use "TOTAL_AMOUNT" or "TOTAL" matching your PL/SQL SELECT statement
                                rs.getBigDecimal("TOTAL_AMOUNT")
                        ))
                );
    }

    public OrderResult processOrder(Long customerId, BigDecimal totalAmount) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("P_CUSTOMER_ID", customerId)
                .addValue("P_TOTAL_AMOUNT", totalAmount);

        Map<String, Object> out = simpleJdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<OrderResult> results = (List<OrderResult>) out.get("P_ORDER_CURSOR");

        if (results == null || results.isEmpty()) {
            throw new IllegalStateException("No order record returned from PL/SQL procedure.");
        }

        return results.get(0);
    }
}