package com.checkout.order.application.integration.event;

import com.checkout.shared.domain.IntegrationEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCheckedOutIntegrationEvent(
        String eventId,
        String orderId,
        String cartId,
        BigDecimal totalAmount,
        String currency,
        Instant occurredAt
) implements IntegrationEvent {}
