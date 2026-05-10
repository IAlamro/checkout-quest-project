-- Partial unique index: at most one INITIATED or AUTHORIZED payment per order (ADR-005 Layer 2).
-- Requires PostgreSQL - H2 (both in-memory and file mode) does not support partial indexes.
-- flyway.target=9 skips this migration on H2; application-level checks still apply.
CREATE UNIQUE INDEX ux_payment_active_per_order
    ON payment_read_model(order_id)
    WHERE status = 'INITIATED' OR status = 'AUTHORIZED';
