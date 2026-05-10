package com.checkout.order.domain;

import com.checkout.order.domain.event.*;
import com.checkout.shared.domain.AggregateRoot;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;
import com.checkout.shared.domain.NonRetryableException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order extends AggregateRoot {

    private OrderId id;
    private String cartId;
    private OrderStatus status;
    private List<OrderItem> items = new ArrayList<>();
    private Money totalAmount;
    private String activePaymentId;

    private Order() {}

    public static Order reconstitute() { return new Order(); }

    public static Order checkout(OrderId orderId, String cartId, List<OrderItem> items, Money totalAmount) {
        Order order = new Order();
        order.record(new OrderCreated(orderId, cartId, items, totalAmount, Instant.now()));
        return order;
    }

    public void requestPayment(String paymentId) {
        if (status == OrderStatus.PENDING_PAYMENT && paymentId.equals(activePaymentId)) return;
        OrderTransition.validate(status, OrderStatus.PENDING_PAYMENT);
        record(new PaymentRequested(id, paymentId, Instant.now()));
    }

    public void markPaid(String paymentId) {
        if (status == OrderStatus.PAID) {
            if (!paymentId.equals(activePaymentId)) {
                throw new DoublePaymentException(id, paymentId, activePaymentId);
            }
            return; // idempotent
        }
        OrderTransition.validate(status, OrderStatus.PAID);
        record(new OrderPaid(id, paymentId, Instant.now()));
    }

    public void markPaymentFailed(String paymentId, String reason) {
        if (status == OrderStatus.PAYMENT_FAILED) return; // idempotent
        OrderTransition.validate(status, OrderStatus.PAYMENT_FAILED);
        record(new OrderPaymentFailed(id, paymentId, reason, Instant.now()));
    }

    @Override
    protected void applyEvent(DomainEvent event) {
        switch (event) {
            case OrderCreated e -> {
                this.id = e.orderId();
                this.cartId = e.cartId();
                this.status = OrderStatus.CREATED;
                this.items = new ArrayList<>(e.items());
                this.totalAmount = e.totalAmount();
            }
            case PaymentRequested e -> {
                this.status = OrderStatus.PENDING_PAYMENT;
                this.activePaymentId = e.paymentId();
            }
            case OrderPaid e -> {
                this.status = OrderStatus.PAID;
                this.activePaymentId = e.paymentId();
            }
            case OrderPaymentFailed e -> {
                this.status = OrderStatus.PAYMENT_FAILED;
                this.activePaymentId = e.paymentId();
            }
            default -> throw new IllegalArgumentException("Unknown Order event: " + event.getClass());
        }
    }

    public OrderId getId() { return id; }
    public String getCartId() { return cartId; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public Money getTotalAmount() { return totalAmount; }
    public String getActivePaymentId() { return activePaymentId; }

    public static class DoublePaymentException extends NonRetryableException {
        public DoublePaymentException(OrderId orderId, String newPaymentId, String existingPaymentId) {
            super("Order %s already paid with payment %s; attempt with %s rejected"
                    .formatted(orderId.value(), existingPaymentId, newPaymentId));
        }
    }
}
