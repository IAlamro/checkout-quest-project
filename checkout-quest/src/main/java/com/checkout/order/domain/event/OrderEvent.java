package com.checkout.order.domain.event;

import com.checkout.shared.domain.DomainEvent;

public sealed interface OrderEvent extends DomainEvent
        permits OrderCreated, PaymentRequested, OrderPaid, OrderPaymentFailed {}
