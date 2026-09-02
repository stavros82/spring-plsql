
CREATE OR REPLACE PACKAGE BODY ORDER_PROCESSOR AS

    PROCEDURE PROCESS_AND_FETCH(
        P_CUSTOMER_ID  IN NUMBER,
        P_TOTAL_AMOUNT IN NUMBER,
        P_ORDER_CURSOR OUT SYS_REFCURSOR
    ) IS
BEGIN
        -- Business rule validation triggering ORA-20001 exception handler
        IF P_TOTAL_AMOUNT IS NULL OR P_TOTAL_AMOUNT <= 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Order total amount must be strictly greater than zero.');
END IF;

        -- Return processed record matching OrderPlsqlRepository column aliases
OPEN P_ORDER_CURSOR FOR
SELECT
    1 AS ORDER_ID,
    'COMPLETED' AS STATUS,
    P_TOTAL_AMOUNT AS TOTAL_AMOUNT
FROM DUAL;

END PROCESS_AND_FETCH;

END ORDER_PROCESSOR;
/