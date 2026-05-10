package com.checkout.order.domain.event;

import com.checkout.order.domain.OrderId;
import com.checkout.shared.domain.DomainEvent;

import java.time.Instant;

public record PaymentRequested(OrderId orderId, String paymentId, Instant occurredAt)
        implements DomainEvent, OrderEvent {}
