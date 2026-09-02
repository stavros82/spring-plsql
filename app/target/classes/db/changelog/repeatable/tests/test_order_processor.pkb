CREATE OR REPLACE PACKAGE BODY test_order_processor IS

    PROCEDURE test_valid_order_processing IS
        v_status_code VARCHAR2(30);
        v_cursor      SYS_REFCURSOR;
        v_order_id    orders.order_id%TYPE;
        v_status      orders.status%TYPE;
        v_total       orders.total_amount%TYPE;
BEGIN
        -- Act
        order_processor.process_and_fetch(
            p_customer_id  => 1001,
            p_total_amount => 150.00,
            p_status_code  => v_status_code,
            p_order_cursor => v_cursor
        );

        -- Assert procedure status output
        ut.expect(v_status_code).to_equal('SUCCESS');

        -- Fetch and assert ref cursor contents
FETCH v_cursor INTO v_order_id, v_status, v_total;
CLOSE v_cursor;

ut.expect(v_order_id).to_be_not_null();
        ut.expect(v_status).to_equal('COMPLETED');
        ut.expect(v_total).to_equal(150.00);
END test_valid_order_processing;

    PROCEDURE test_negative_amount_throws_exception IS
        v_status_code VARCHAR2(30);
        v_cursor      SYS_REFCURSOR;
BEGIN
        order_processor.process_and_fetch(
            p_customer_id  => 1001,
            p_total_amount => -50.00,
            p_status_code  => v_status_code,
            p_order_cursor => v_cursor
        );
END test_negative_amount_throws_exception;

    PROCEDURE test_zero_amount_throws_exception IS
        v_status_code VARCHAR2(30);
        v_cursor      SYS_REFCURSOR;
BEGIN
        order_processor.process_and_fetch(
            p_customer_id  => 1001,
            p_total_amount => 0.00,
            p_status_code  => v_status_code,
            p_order_cursor => v_cursor
        );
END test_zero_amount_throws_exception;

END test_order_processor;
