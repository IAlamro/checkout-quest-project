package com.checkout.payment.application.integration.event;

import com.checkout.shared.domain.IntegrationEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentInitiatedIntegrationEvent(
        String eventId,
        String paymentId,
        String orderId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements IntegrationEvent {}
