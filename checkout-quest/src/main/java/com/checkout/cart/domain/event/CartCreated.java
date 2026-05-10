package com.checkout.cart.domain.event;

import com.checkout.cart.domain.CartId;
import com.checkout.shared.domain.DomainEvent;

import java.time.Instant;

public record CartCreated(CartId cartId, Instant occurredAt) implements DomainEvent, CartEvent {}
