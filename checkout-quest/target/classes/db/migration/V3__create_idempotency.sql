CREATE TABLE idempotency_records (
    idempotency_key VARCHAR(255)  NOT NULL,
    endpoint        VARCHAR(500)  NOT NULL,
    fingerprint     VARCHAR(255)  NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    status_code     INT,
    response_body   CLOB,
    created_at      TIMESTAMP     NOT NULL,
    completed_at    TIMESTAMP,
    CONSTRAINT pk_idempotency PRIMARY KEY (idempotency_key)
);
