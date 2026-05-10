package com.checkout.payment.domain.event;

import com.checkout.shared.domain.DomainEvent;

public sealed interface PaymentEvent extends DomainEvent
        permits PaymentInitiated, PaymentAuthorized, PaymentConfirmed, PaymentFailed {}
