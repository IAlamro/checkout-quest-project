package com.checkout.payment.application.usecase.retrypayment;

import com.checkout.shared.cqrs.Command;

public record RetryPaymentCommand(String orderId, String idempotencyKey) implements Command {}
