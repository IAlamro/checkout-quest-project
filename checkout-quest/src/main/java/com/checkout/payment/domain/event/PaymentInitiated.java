package com.checkout.payment.domain.event;

import com.checkout.payment.domain.PaymentId;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;

import java.time.Instant;

public record PaymentInitiated(
        PaymentId paymentId, String orderId, Money amount, String idempotencyKey, Instant occurredAt
) implements DomainEvent, PaymentEvent {}
