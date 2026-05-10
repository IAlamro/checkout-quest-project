package com.checkout.cart.application.api;

import com.checkout.shared.domain.Money;

public record CartItemSnapshot(String productId, int quantity, Money unitPrice) {}
