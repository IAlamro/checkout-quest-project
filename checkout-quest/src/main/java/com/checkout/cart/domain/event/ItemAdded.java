package com.checkout.cart.domain.event;

import com.checkout.cart.domain.CartId;
import com.checkout.cart.domain.CartItem;
import com.checkout.shared.domain.DomainEvent;

import java.time.Instant;

public record ItemAdded(CartId cartId, CartItem item, Instant occurredAt) implements DomainEvent, CartEvent {}
