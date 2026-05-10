package com.checkout.cart.domain;

import com.checkout.shared.domain.Money;

public record CartItem(String itemId, ProductRef product, int quantity) {
    public CartItem {
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("itemId required");
        if (product == null) throw new IllegalArgumentException("product required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
    }

    public Money lineTotal() {
        return Money.of(
                product.unitPrice().amount().multiply(java.math.BigDecimal.valueOf(quantity)),
                product.unitPrice().currency().getCurrencyCode()
        );
    }
}
