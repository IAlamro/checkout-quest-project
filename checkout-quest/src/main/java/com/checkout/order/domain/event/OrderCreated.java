package com.checkout.order.domain.event;

import com.checkout.order.domain.OrderId;
import com.checkout.order.domain.OrderItem;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;

import java.time.Instant;
import java.util.List;

public record OrderCreated(
        OrderId orderId,
        String cartId,
        List<OrderItem> items,
        Money totalAmount,
        Instant occurredAt
) implements DomainEvent, OrderEvent {}
