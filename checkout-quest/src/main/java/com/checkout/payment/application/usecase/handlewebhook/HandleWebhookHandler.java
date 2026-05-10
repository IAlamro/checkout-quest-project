package com.checkout.payment.application.usecase.handlewebhook;

import com.checkout.payment.application.integration.event.PaymentConfirmedIntegrationEvent;
import com.checkout.payment.application.integration.event.PaymentFailedIntegrationEvent;
import com.checkout.payment.domain.Payment;
import com.checkout.payment.domain.WebhookType;
import com.checkout.payment.infrastructure.PaymentAggregateRepository;
import com.checkout.payment.infrastructure.projection.PaymentJpaRepository;
import com.checkout.payment.infrastructure.projection.ProcessedWebhookEventEntity;
import com.checkout.payment.infrastructure.projection.ProcessedWebhookJpaRepository;
import com.checkout.shared.cqrs.CommandHandler;
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
public class HandleWebhookHandler implements CommandHandler<HandleWebhookCommand, Void> {

    private final PaymentAggregateRepository paymentRepository;
    private final PaymentJpaRepository paymentJpaRepository;
    private final ProcessedWebhookJpaRepository webhookDedupRepository;
    private final OutboxStore outboxStore;
    private final JsonMapper jsonMapper;

    @Override
    @Transactional
    public Void handle(HandleWebhookCommand command) {
        // Layer 3 idempotency: webhook event dedup (SELECT FOR UPDATE semantics via PK insert)
        var paymentOpt = paymentJpaRepository.findByProviderRef(command.getProviderRef());
        if (paymentOpt.isEmpty()) {
            return null; // unknown providerRef - ignore
        }
        String paymentId = paymentOpt.get().getPaymentId();

        try {
            webhookDedupRepository.saveAndFlush(new ProcessedWebhookEventEntity(
                    command.getEventId(), paymentId, command.getType().name(), Instant.now()));
        } catch (DataIntegrityViolationException e) {
            return null; // duplicate webhook - idempotent
        }

        Payment payment = paymentRepository.load(paymentId);
        if (payment.isTerminal()) return null;

        applyWebhookToPayment(payment, command);
        paymentRepository.save(payment);

        enqueueIntegrationEvent(payment, command);
        return null;
    }

    private void applyWebhookToPayment(Payment payment, HandleWebhookCommand command) {
        switch (command.getType()) {
            case AUTHORIZED -> payment.authorize(new com.checkout.payment.domain.ProviderRef(command.getProviderRef()));
            case CONFIRMED -> payment.confirm();
            case FAILED -> payment.fail(command.getReason() != null ? command.getReason() : "Provider reported failure");
        }
    }

    private void enqueueIntegrationEvent(Payment payment, HandleWebhookCommand command) {
        if (command.getType() == WebhookType.CONFIRMED) {
            var event = new PaymentConfirmedIntegrationEvent(
                    UuidV7.generateAsString(), payment.getId().value(), payment.getOrderId(),
                    payment.getAmount().amount(), payment.getAmount().currency().getCurrencyCode(), Instant.now()
            );
            outboxStore.enqueue(new OutboxMessage(UuidV7.generateAsString(), "payment",
                    payment.getId().value(), event.eventType(), jsonMapper.serialize(event), Instant.now(), 0));
        } else if (command.getType() == WebhookType.FAILED) {
            var event = new PaymentFailedIntegrationEvent(
                    UuidV7.generateAsString(), payment.getId().value(), payment.getOrderId(),
                    payment.getAmount().amount(), payment.getAmount().currency().getCurrencyCode(),
                    command.getReason(), Instant.now()
            );
            outboxStore.enqueue(new OutboxMessage(UuidV7.generateAsString(), "payment",
                    payment.getId().value(), event.eventType(), jsonMapper.serialize(event), Instant.now(), 0));
        }
    }
}
