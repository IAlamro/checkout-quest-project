package com.checkout.shared.infrastructure.outbox;

import com.checkout.shared.domain.NonRetryableException;
import com.checkout.shared.outbox.OutboxMessage;
import com.checkout.shared.outbox.OutboxStore;
import com.checkout.shared.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private static final int BATCH_SIZE = 50;

    private final OutboxStore outboxStore;
    private final ApplicationEventPublisher eventPublisher;
    private final JsonMapper jsonMapper;

    @Value("${scheduling.outbox-max-retries:5}")
    private int maxRetries;

    @Scheduled(fixedDelayString = "${scheduling.outbox-relay-interval-ms:500}")
    @Transactional
    public void relay() {
        List<OutboxMessage> messages = outboxStore.pollUndispatched(BATCH_SIZE);
        for (OutboxMessage message : messages) {
            dispatch(message);
        }
    }

    private void dispatch(OutboxMessage message) {
        try {
            Object event = jsonMapper.deserialize(message.payload(), message.eventType());
            eventPublisher.publishEvent(event);
            outboxStore.markDispatched(message.id());
        } catch (NonRetryableException e) {
            log.error("Dead-lettering outbox message {} - non-retryable business violation: {}",
                    message.id(), e.getMessage());
            outboxStore.markDead(message.id(), "NON_RETRYABLE: " + e.getMessage());
        } catch (Exception e) {
            int nextAttempt = message.attemptCount() + 1;
            if (nextAttempt >= maxRetries) {
                log.error("Dead-lettering outbox message {} after {} attempts: {}",
                        message.id(), nextAttempt, e.getMessage(), e);
                outboxStore.markFailed(message.id(), e.getMessage());
                outboxStore.markDead(message.id(), "RETRY_EXHAUSTED after %d attempts: %s".formatted(nextAttempt, e.getMessage()));
            } else {
                log.warn("Outbox message {} failed (attempt {}/{}): {}",
                        message.id(), nextAttempt, maxRetries, e.getMessage(), e);
                outboxStore.markFailed(message.id(), e.getMessage());
            }
        }
    }
}
