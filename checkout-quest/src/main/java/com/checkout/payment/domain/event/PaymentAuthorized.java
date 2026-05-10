package com.checkout.payment.domain.event;

import com.checkout.payment.domain.PaymentId;
import com.checkout.payment.domain.ProviderRef;
import com.checkout.shared.domain.DomainEvent;

import java.time.Instant;

public record PaymentAuthorized(PaymentId paymentId, ProviderRef providerRef, Instant occurredAt)
        implements DomainEvent, PaymentEvent {}
