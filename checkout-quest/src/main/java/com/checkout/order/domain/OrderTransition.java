package com.checkout.order.domain;

import java.util.Map;
import java.util.Set;

/**
 * Table-driven state machine policy for Order transitions.
 * Domain-owned - no framework dependencies.
 */
final class OrderTransition {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            OrderStatus.CREATED,         Set.of(OrderStatus.PENDING_PAYMENT),
            OrderStatus.PENDING_PAYMENT, Set.of(OrderStatus.PAID, OrderStatus.PAYMENT_FAILED),
            OrderStatus.PAYMENT_FAILED,  Set.of(OrderStatus.PENDING_PAYMENT)
    );

    private OrderTransition() {}

    static void validate(OrderStatus from, OrderStatus to) {
        Set<OrderStatus> allowed = ALLOWED.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidOrderTransitionException(from, to);
        }
    }

}
