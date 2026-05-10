package com.checkout.ledger.domain;

public record JournalEntryId(String value) {
    public JournalEntryId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("JournalEntryId must not be blank");
    }

    @Override
    public String toString() { return value; }
}
