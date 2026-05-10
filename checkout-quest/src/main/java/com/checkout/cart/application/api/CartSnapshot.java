package com.checkout.cart.application.api;

import com.checkout.shared.domain.Money;

import java.util.List;

public record CartSnapshot(String cartId, String status, List<CartItemSnapshot> items, Money totalAmount) {}
