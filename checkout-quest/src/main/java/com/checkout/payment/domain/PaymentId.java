package com.checkout.payment.domain;

public record PaymentId(String value) {
    public PaymentId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("PaymentId must not be blank");
    }

    @Override
    public String toString() { return value; }
}
