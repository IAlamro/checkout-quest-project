package com.checkout.domain;

import com.checkout.payment.domain.Payment;
import com.checkout.payment.domain.PaymentId;
import com.checkout.payment.domain.PaymentStatus;
import com.checkout.payment.domain.ProviderRef;
import com.checkout.payment.domain.WebhookType;
import com.checkout.shared.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PaymentDomainTest {

    private Payment freshPayment() {
        return Payment.initiate(
                new PaymentId("pay-1"),
                "order-1",
                Money.usd(100.0),
                "idem-key-1"
        );
    }

    // Happy path 

    @Test
    void initiate_creates_payment_in_INITIATED_state() {
        Payment p = freshPayment();
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.INITIATED);
    }

    @Test
    void authorize_transitions_INITIATED_to_AUTHORIZED() {
        Payment p = freshPayment();
        p.authorize(new ProviderRef("ref-1"));
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void confirm_transitions_AUTHORIZED_to_CONFIRMED() {
        Payment p = freshPayment();
        p.authorize(new ProviderRef("ref-1"));
        p.confirm();
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.CONFIRMED);
        assertThat(p.isTerminal()).isTrue();
    }

    @Test
    void fail_transitions_INITIATED_to_FAILED() {
        Payment p = freshPayment();
        p.fail("Declined by bank");
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(p.isTerminal()).isTrue();
    }

    // Duplicate webhook idempotency (Flow 3 requirement) 

    @Test
    void duplicate_CONFIRMED_webhook_on_terminal_payment_is_ignored() {
        Payment p = freshPayment();
        p.authorize(new ProviderRef("ref-1"));
        p.confirm();
        int eventsBefore = p.getUncommittedEvents().size();

        // Second CONFIRMED webhook - must not throw and must not add a new event
        p.confirm();

        assertThat(p.getUncommittedEvents()).hasSize(eventsBefore);
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.CONFIRMED);
    }

    @Test
    void duplicate_FAILED_webhook_on_terminal_payment_is_ignored() {
        Payment p = freshPayment();
        p.fail("Declined");
        int eventsBefore = p.getUncommittedEvents().size();

        p.fail("Declined again");

        assertThat(p.getUncommittedEvents()).hasSize(eventsBefore);
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void CONFIRMED_webhook_after_FAILED_is_ignored_not_exception() {
        Payment p = freshPayment();
        p.fail("Declined");

        assertThatNoException().isThrownBy(p::confirm);
        assertThat(p.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    // Invalid transitions 

    @Test
    void confirm_without_prior_authorize_throws_InvalidPaymentStateException() {
        Payment p = freshPayment();
        assertThatThrownBy(p::confirm)
                .isInstanceOf(Payment.InvalidPaymentStateException.class);
    }

    @Test
    void authorize_after_confirm_throws_InvalidPaymentStateException() {
        Payment p = freshPayment();
        p.authorize(new ProviderRef("ref-1"));
        p.confirm();
        assertThatThrownBy(() -> p.authorize(new ProviderRef("ref-2")))
                .isInstanceOf(Payment.InvalidPaymentStateException.class)
                .hasMessageContaining("CONFIRMED");
    }
}
