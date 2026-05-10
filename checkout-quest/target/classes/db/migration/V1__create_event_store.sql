CREATE TABLE events (
    event_id    VARCHAR(36)   NOT NULL,
    stream_id   VARCHAR(36)   NOT NULL,
    stream_type VARCHAR(100)  NOT NULL,
    version     BIGINT        NOT NULL,
    event_type  VARCHAR(255)  NOT NULL,
    payload     CLOB          NOT NULL,
    occurred_at TIMESTAMP     NOT NULL,
    recorded_at TIMESTAMP     NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (event_id),
    CONSTRAINT ux_events_stream_version UNIQUE (stream_id, stream_type, version)
);

CREATE INDEX idx_events_stream ON events (stream_id, stream_type);
