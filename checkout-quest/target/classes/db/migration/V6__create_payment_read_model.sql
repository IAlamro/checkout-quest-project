CREATE TABLE payment_read_model (
    payment_id       VARCHAR(36)    NOT NULL,
    order_id         VARCHAR(36)    NOT NULL,
    status           VARCHAR(20)    NOT NULL,
    amount           DECIMAL(19,2)  NOT NULL,
    currency         VARCHAR(3)     NOT NULL,
    provider_ref     VARCHAR(255),
    idempotency_key  VARCHAR(255),
    version          BIGINT         NOT NULL DEFAULT 0,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL,
    CONSTRAINT pk_payment_read_model PRIMARY KEY (payment_id)
);

CREATE TABLE processed_webhook_events (
    event_id     VARCHAR(255)  NOT NULL,
    payment_id   VARCHAR(36)   NOT NULL,
    event_type   VARCHAR(50)   NOT NULL,
    processed_at TIMESTAMP     NOT NULL,
    CONSTRAINT pk_processed_webhooks PRIMARY KEY (event_id)
);
