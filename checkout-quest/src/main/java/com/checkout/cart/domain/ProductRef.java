package com.checkout.cart.domain;

import com.checkout.shared.domain.Money;

public record ProductRef(String productId, Money unitPrice) {
    public ProductRef {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("productId required");
        if (unitPrice == null) throw new IllegalArgumentException("unitPrice required");
    }
}
