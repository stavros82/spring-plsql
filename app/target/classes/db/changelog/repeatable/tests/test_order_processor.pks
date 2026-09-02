CREATE OR REPLACE PACKAGE test_order_processor IS
    -- %suite(Order Processor Test Suite)
    -- %suitepath(orders)

    -- %test(Processes valid order successfully)
    PROCEDURE test_valid_order_processing;

    -- %test(Raises ORA-20001 when total amount is negative)
    -- %throws(-20001)
    PROCEDURE test_negative_amount_throws_exception;

    -- %test(Raises ORA-20001 when total amount is zero)
    -- %throws(-20001)
    PROCEDURE test_zero_amount_throws_exception;
END test_order_processor;
