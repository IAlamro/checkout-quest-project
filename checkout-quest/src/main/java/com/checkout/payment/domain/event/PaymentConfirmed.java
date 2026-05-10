package com.checkout.payment.domain.event;

import com.checkout.payment.domain.PaymentId;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;

import java.time.Instant;

public record PaymentConfirmed(PaymentId paymentId, String orderId, Money amount, Instant occurredAt)
        implements DomainEvent, PaymentEvent {}
