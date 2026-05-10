package com.checkout.cart.domain;

public record CartId(String value) {
    public CartId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("CartId must not be blank");
    }

    @Override
    public String toString() { return value; }
}
