package com.checkout.domain;

import com.checkout.order.domain.InvalidOrderTransitionException;
import com.checkout.order.domain.Order;
import com.checkout.order.domain.OrderItem;
import com.checkout.order.domain.OrderId;
import com.checkout.order.domain.OrderStatus;
import com.checkout.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderStateMachineTest {

    private static final String CART_ID   = "cart-1";
    private static final String PAYMENT_A = "pay-A";
    private static final String PAYMENT_B = "pay-B";

    private Order freshOrder() {
        Money price = Money.usd(50.0);
        List<OrderItem> items = List.of(new OrderItem("prod-1", 2, price));
        return Order.checkout(new OrderId("order-1"), CART_ID, items, Money.usd(100.0));
    }

    // Initial state

    @Test
    void checkout_creates_order_in_CREATED_state() {
        Order order = freshOrder();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    // CREATED -> PENDING_PAYMENT 

    @Test
    void requestPayment_transitions_CREATED_to_PENDING_PAYMENT() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getActivePaymentId()).isEqualTo(PAYMENT_A);
    }

    @Test
    void requestPayment_is_idempotent_when_already_PENDING_PAYMENT_with_same_paymentId() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.requestPayment(PAYMENT_A); // second call - must not throw or add an event
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getUncommittedEvents()).hasSize(2); // OrderCreated + 1x PaymentRequested only
    }

    // PENDING_PAYMENT -> PAID 

    @Test
    void markPaid_transitions_PENDING_PAYMENT_to_PAID() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.markPaid(PAYMENT_A);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void markPaid_is_idempotent_when_already_PAID_with_same_paymentId() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.markPaid(PAYMENT_A);
        order.markPaid(PAYMENT_A); // duplicate - must not throw
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void markPaid_with_different_paymentId_on_PAID_order_throws_DoublePaymentException() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.markPaid(PAYMENT_A);
        assertThatThrownBy(() -> order.markPaid(PAYMENT_B))
                .isInstanceOf(Order.DoublePaymentException.class);
    }

    // PENDING_PAYMENT -> PAYMENT_FAILED 

    @Test
    void markPaymentFailed_transitions_PENDING_PAYMENT_to_PAYMENT_FAILED() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.markPaymentFailed(PAYMENT_A, "Declined");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    @Test
    void markPaymentFailed_is_idempotent_when_already_PAYMENT_FAILED() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.markPaymentFailed(PAYMENT_A, "Declined");
        order.markPaymentFailed(PAYMENT_A, "Declined again"); // must not throw
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
    }

    // PAYMENT_FAILED -> PENDING_PAYMENT (retry) 

    @Test
    void requestPayment_from_PAYMENT_FAILED_transitions_to_PENDING_PAYMENT() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.markPaymentFailed(PAYMENT_A, "Declined");
        order.requestPayment(PAYMENT_B);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getActivePaymentId()).isEqualTo(PAYMENT_B);
    }

    // Invalid transitions 

    @Test
    void markPaid_from_CREATED_is_forbidden() {
        Order order = freshOrder();
        assertThatThrownBy(() -> order.markPaid(PAYMENT_A))
                .isInstanceOf(InvalidOrderTransitionException.class);
    }

    @Test
    void markPaymentFailed_from_CREATED_is_forbidden() {
        Order order = freshOrder();
        assertThatThrownBy(() -> order.markPaymentFailed(PAYMENT_A, "reason"))
                .isInstanceOf(InvalidOrderTransitionException.class);
    }

    @Test
    void requestPayment_from_PAID_is_forbidden() {
        Order order = freshOrder();
        order.requestPayment(PAYMENT_A);
        order.markPaid(PAYMENT_A);
        assertThatThrownBy(() -> order.requestPayment(PAYMENT_B))
                .isInstanceOf(InvalidOrderTransitionException.class);
    }
}
