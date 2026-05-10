package com.checkout.ledger.domain;

public record AccountCode(String value) {
    public static final AccountCode CASH = new AccountCode("1100");
    public static final AccountCode REVENUE = new AccountCode("4000");
    public static final AccountCode TRANSACTION_EXPENSE = new AccountCode("5000");

    public AccountCode {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("AccountCode must not be blank");
    }

    @Override
    public String toString() { return value; }
}
