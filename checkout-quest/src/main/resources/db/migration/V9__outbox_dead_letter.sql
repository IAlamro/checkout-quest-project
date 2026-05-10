-- Dead-letter support for the outbox relay.
-- dead=TRUE means the relay will never pick this row up again.
-- dead_reason captures why: either a non-retryable business exception or retry exhaustion.
ALTER TABLE outbox ADD COLUMN dead      BOOLEAN      NOT NULL DEFAULT FALSE;
ALTER TABLE outbox ADD COLUMN dead_reason VARCHAR(1000);
