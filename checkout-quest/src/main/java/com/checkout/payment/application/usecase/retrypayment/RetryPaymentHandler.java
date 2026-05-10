package com.checkout.payment.application.usecase.retrypayment;

import com.checkout.payment.application.usecase.initiatepayment.InitiatePaymentCommand;
import com.checkout.payment.application.usecase.initiatepayment.InitiatePaymentHandler;
import com.checkout.payment.application.usecase.initiatepayment.PaymentResult;
import com.checkout.payment.infrastructure.projection.PaymentJpaRepository;
import com.checkout.shared.cqrs.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetryPaymentHandler implements CommandHandler<RetryPaymentCommand, PaymentResult> {

    private final PaymentJpaRepository paymentJpaRepository;
    private final InitiatePaymentHandler initiatePaymentHandler;

    @Override
    @Transactional
    public PaymentResult handle(RetryPaymentCommand command) {
        var lastPayment = paymentJpaRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new IllegalStateException("No payment found for order " + command.orderId()));

        if (!"FAILED".equals(lastPayment.getStatus())) {
            throw new IllegalStateException(
                    "Retry requires last payment status FAILED, current: " + lastPayment.getStatus());
        }

        // Delegates to InitiatePaymentHandler, which publishes PaymentInitiatedIntegrationEvent
        // → OutboxRelay → PaymentInitiatedOrderListener transitions Order back to PENDING_PAYMENT
        return initiatePaymentHandler.handle(InitiatePaymentCommand.builder()
                .orderId(command.orderId())
                .amount(lastPayment.getAmount())
                .currency(lastPayment.getCurrency())
                .idempotencyKey(command.idempotencyKey())
                .build());
    }
}
