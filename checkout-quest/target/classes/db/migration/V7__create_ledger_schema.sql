CREATE TABLE ledger_account (
    account_id      VARCHAR(36)    NOT NULL,
    code            VARCHAR(20)    NOT NULL,
    name            VARCHAR(100)   NOT NULL,
    type            VARCHAR(20)    NOT NULL,
    normal_balance  VARCHAR(10)    NOT NULL,
    balance_amount  DECIMAL(19,2)  NOT NULL DEFAULT 0,
    currency        VARCHAR(3)     NOT NULL,
    version         BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT pk_ledger_account PRIMARY KEY (account_id),
    CONSTRAINT ux_ledger_account_code UNIQUE (code)
);

CREATE TABLE ledger_journal_entry (
    entry_id        VARCHAR(36)    NOT NULL,
    reference_id    VARCHAR(255)   NOT NULL,
    reference_type  VARCHAR(50)    NOT NULL,
    posted_at       TIMESTAMP      NOT NULL,
    CONSTRAINT pk_ledger_journal_entry PRIMARY KEY (entry_id),
    CONSTRAINT ux_ledger_journal_ref UNIQUE (reference_id, reference_type)
);

CREATE TABLE ledger_entry_line (
    id           VARCHAR(36)    NOT NULL,
    entry_id     VARCHAR(36)    NOT NULL,
    account_code VARCHAR(20)    NOT NULL,
    amount       DECIMAL(19,2)  NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    side         VARCHAR(10)    NOT NULL,
    description  VARCHAR(500),
    CONSTRAINT pk_ledger_entry_line PRIMARY KEY (id),
    CONSTRAINT fk_entry_line_entry FOREIGN KEY (entry_id) REFERENCES ledger_journal_entry(entry_id)
);
