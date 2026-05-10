CREATE TABLE outbox (
    id             VARCHAR(36)   NOT NULL,
    aggregate_type VARCHAR(100)  NOT NULL,
    aggregate_id   VARCHAR(36)   NOT NULL,
    event_type     VARCHAR(255)  NOT NULL,
    payload        CLOB          NOT NULL,
    created_at     TIMESTAMP     NOT NULL,
    dispatched_at  TIMESTAMP,
    attempt_count  INT           NOT NULL DEFAULT 0,
    last_error     VARCHAR(1000),
    CONSTRAINT pk_outbox PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_undispatched ON outbox (created_at);
