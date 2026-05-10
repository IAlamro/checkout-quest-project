package com.checkout.order.domain;

import com.checkout.shared.domain.NonRetryableException;

public class InvalidOrderTransitionException extends NonRetryableException {
    public InvalidOrderTransitionException(OrderStatus from, OrderStatus to) {
        super("Invalid order transition: %s → %s".formatted(from, to));
    }
}
