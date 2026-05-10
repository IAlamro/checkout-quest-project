package com.checkout.cart.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartResponse(
        String cartId,
        String status,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        Instant lockedAt,
        List<CartItemResponse> items
) {}
