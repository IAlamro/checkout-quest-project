package com.checkout.cart.domain.event;

import com.checkout.shared.domain.DomainEvent;

public sealed interface CartEvent extends DomainEvent permits CartCreated, ItemAdded, CartLocked {}
