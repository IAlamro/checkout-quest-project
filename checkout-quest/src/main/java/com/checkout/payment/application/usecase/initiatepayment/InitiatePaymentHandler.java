package com.checkout.payment.application.usecase.initiatepayment;

import com.checkout.payment.application.integration.event.PaymentInitiatedIntegrationEvent;
import com.checkout.payment.domain.Payment;
import com.checkout.payment.domain.PaymentId;
import com.checkout.payment.domain.ProviderRef;
import com.checkout.payment.domain.port.PaymentGateway;
import com.checkout.payment.infrastructure.PaymentAggregateRepository;
import com.checkout.shared.cqrs.CommandHandler;
import com.checkout.shared.domain.Money;
import com.checkout.shared.outbox.OutboxMessage;
import com.checkout.shared.outbox.OutboxStore;
import com.checkout.shared.util.JsonMapper;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InitiatePaymentHandler implements CommandHandler<InitiatePaymentCommand, PaymentResult> {

    private final PaymentAggregateRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final OutboxStore outboxStore;
    private final JsonMapper jsonMapper;

    @Override
    @Transactional
    public PaymentResult handle(InitiatePaymentCommand command) {
        PaymentId paymentId = new PaymentId(UuidV7.generateAsString());
        Money amount = Money.of(command.getAmount(), command.getCurrency());

        Payment payment = Payment.initiate(paymentId, command.getOrderId(), amount, command.getIdempotencyKey());
        try {
            paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            throw new ActivePaymentExistsException(command.getOrderId());
        }

        ProviderRef ref = paymentGateway.createIntent(paymentId.value(), amount);

        payment = paymentRepository.load(paymentId.value());
        payment.authorize(ref);
        paymentRepository.save(payment);

        // Notify Order BC via outbox - no direct order.* import (ADR boundary rule)
        var intEvent = new PaymentInitiatedIntegrationEvent(
                UuidV7.generateAsString(), paymentId.value(), command.getOrderId(),
                amount.amount(), amount.currency().getCurrencyCode(), Instant.now()
        );
        outboxStore.enqueue(new OutboxMessage(UuidV7.generateAsString(), "payment",
                paymentId.value(), intEvent.eventType(), jsonMapper.serialize(intEvent), Instant.now(), 0));

        return new PaymentResult(paymentId.value(), ref.externalId());
    }

    public static class ActivePaymentExistsException extends RuntimeException {
        public ActivePaymentExistsException(String orderId) {
            super("An active payment already exists for order %s".formatted(orderId));
        }
    }
}
