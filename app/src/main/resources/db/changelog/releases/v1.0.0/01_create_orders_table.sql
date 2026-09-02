
CREATE TABLE orders (
                        order_id     NUMBER GENERATED ALWAYS AS IDENTITY,
                        customer_id  NUMBER NOT NULL,
                        status       VARCHAR2(30) NOT NULL,
                        total_amount NUMBER(10, 2) NOT NULL,
                        created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        CONSTRAINT pk_orders PRIMARY KEY (order_id)
);
/