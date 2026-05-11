package com.checkout.payment.infrastructure.gateway;

public class PaymentProviderUnavailableException extends RuntimeException {

    public PaymentProviderUnavailableException(String paymentId, Throwable cause) {
        super("Payment provider is unavailable — cannot create intent for payment " + paymentId, cause);
    }
}
