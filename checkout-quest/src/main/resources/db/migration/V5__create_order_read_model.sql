CREATE TABLE order_read_model (
    order_id     VARCHAR(36)    NOT NULL,
    cart_id      VARCHAR(36)    NOT NULL,
    status       VARCHAR(30)    NOT NULL,
    total_amount DECIMAL(19,2)  NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    version      BIGINT         NOT NULL DEFAULT 0,
    created_at   TIMESTAMP      NOT NULL,
    CONSTRAINT pk_order_read_model PRIMARY KEY (order_id)
);

CREATE TABLE order_item_read_model (
    id                  VARCHAR(36)    NOT NULL,
    order_id            VARCHAR(36)    NOT NULL,
    product_id          VARCHAR(100)   NOT NULL,
    quantity            INT            NOT NULL,
    unit_price_amount   DECIMAL(19,2)  NOT NULL,
    unit_price_currency VARCHAR(3)     NOT NULL,
    CONSTRAINT pk_order_item_read_model PRIMARY KEY (id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES order_read_model(order_id)
);
