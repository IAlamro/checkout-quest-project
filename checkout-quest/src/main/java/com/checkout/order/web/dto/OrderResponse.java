package com.checkout.order.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String orderId,
        String cartId,
        String status,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        List<OrderItemResponse> items
) {}
