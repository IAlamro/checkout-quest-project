package com.checkout.order.domain;

import com.checkout.shared.domain.Money;

public record OrderItem(String productId, int quantity, Money unitPrice) {}
