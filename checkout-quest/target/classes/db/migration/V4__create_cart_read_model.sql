CREATE TABLE cart_read_model (
    cart_id    VARCHAR(36)    NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    currency   VARCHAR(3)     NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    version    BIGINT         NOT NULL DEFAULT 0,
    created_at TIMESTAMP      NOT NULL,
    locked_at  TIMESTAMP,
    CONSTRAINT pk_cart_read_model PRIMARY KEY (cart_id)
);

CREATE TABLE cart_item_read_model (
    id                  VARCHAR(36)    NOT NULL,
    cart_id             VARCHAR(36)    NOT NULL,
    product_id          VARCHAR(100)   NOT NULL,
    quantity            INT            NOT NULL,
    unit_price_amount   DECIMAL(19,2)  NOT NULL,
    unit_price_currency VARCHAR(3)     NOT NULL,
    CONSTRAINT pk_cart_item_read_model PRIMARY KEY (id),
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart_read_model(cart_id)
);
