package com.checkout.payment.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String paymentId,
        String orderId,
        String status,
        BigDecimal amount,
        String currency,
        String providerRef,
        String idempotencyKey,
        Instant createdAt,
        Instant updatedAt
) {}
