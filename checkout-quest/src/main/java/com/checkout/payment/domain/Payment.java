package com.checkout.payment.domain;

import com.checkout.payment.domain.event.*;
import com.checkout.shared.domain.AggregateRoot;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;
import com.checkout.shared.domain.NonRetryableException;

import java.time.Instant;

public class Payment extends AggregateRoot {

    private PaymentId id;
    private String orderId;
    private PaymentStatus status;
    private Money amount;
    private ProviderRef providerRef;
    private String idempotencyKey;

    private Payment() {}

    public static Payment reconstitute() { return new Payment(); }

    public static Payment initiate(PaymentId paymentId, String orderId, Money amount, String idempotencyKey) {
        Payment payment = new Payment();
        payment.record(new PaymentInitiated(paymentId, orderId, amount, idempotencyKey, Instant.now()));
        return payment;
    }

    public void authorize(ProviderRef ref) {
        // Idempotent when already authorized - sync gateway response and async webhook may both arrive
        if (status == PaymentStatus.AUTHORIZED) return;
        requireNonTerminal(WebhookType.AUTHORIZED);
        if (!WebhookOrderingPolicy.isLegalTransition(status, WebhookType.AUTHORIZED)) {
            throw new InvalidPaymentStateException(id, status, WebhookType.AUTHORIZED);
        }
        record(new PaymentAuthorized(id, ref, Instant.now()));
    }

    public void confirm() {
        // Idempotent when already terminal - the provider may redeliver CONFIRMED
        if (isTerminal()) return;
        if (!WebhookOrderingPolicy.isLegalTransition(status, WebhookType.CONFIRMED)) {
            throw new InvalidPaymentStateException(id, status, WebhookType.CONFIRMED);
        }
        record(new PaymentConfirmed(id, orderId, amount, Instant.now()));
    }

    public void fail(String reason) {
        // Idempotent when already terminal - the provider may redeliver FAILED
        if (isTerminal()) return;
        if (!WebhookOrderingPolicy.isLegalTransition(status, WebhookType.FAILED)) {
            throw new InvalidPaymentStateException(id, status, WebhookType.FAILED);
        }
        record(new PaymentFailed(id, orderId, amount, reason, Instant.now()));
    }

    private void requireNonTerminal(WebhookType type) {
        if (isTerminal()) {
            throw new InvalidPaymentStateException(id, status, type);
        }
    }

    public boolean isTerminal() {
        return status == PaymentStatus.CONFIRMED || status == PaymentStatus.FAILED;
    }

    @Override
    protected void applyEvent(DomainEvent event) {
        switch (event) {
            case PaymentInitiated e -> {
                this.id = e.paymentId();
                this.orderId = e.orderId();
                this.status = PaymentStatus.INITIATED;
                this.amount = e.amount();
                this.idempotencyKey = e.idempotencyKey();
            }
            case PaymentAuthorized e -> {
                this.status = PaymentStatus.AUTHORIZED;
                this.providerRef = e.providerRef();
            }
            case PaymentConfirmed e -> this.status = PaymentStatus.CONFIRMED;
            case PaymentFailed e -> this.status = PaymentStatus.FAILED;
            default -> throw new IllegalArgumentException("Unknown Payment event: " + event.getClass());
        }
    }

    public PaymentId getId() { return id; }
    public String getOrderId() { return orderId; }
    public PaymentStatus getStatus() { return status; }
    public Money getAmount() { return amount; }
    public ProviderRef getProviderRef() { return providerRef; }
    public String getIdempotencyKey() { return idempotencyKey; }

    public static class InvalidPaymentStateException extends NonRetryableException {
        public InvalidPaymentStateException(PaymentId id, PaymentStatus status, WebhookType type) {
            super("Payment %s in state %s cannot process webhook %s".formatted(id.value(), status, type));
        }
    }
}
