package com.checkout.payment.domain.event;

import com.checkout.payment.domain.PaymentId;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;

import java.time.Instant;

public record PaymentFailed(PaymentId paymentId, String orderId, Money amount, String reason, Instant occurredAt)
        implements DomainEvent, PaymentEvent {}
