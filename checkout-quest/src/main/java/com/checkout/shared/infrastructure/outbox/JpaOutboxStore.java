package com.checkout.shared.infrastructure.outbox;

import com.checkout.shared.outbox.OutboxMessage;
import com.checkout.shared.outbox.OutboxStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaOutboxStore implements OutboxStore {

    private final OutboxJpaRepository repository;

    @Override
    public void enqueue(OutboxMessage message) {
        OutboxEntity entity = new OutboxEntity(
                message.id(),
                message.aggregateType(),
                message.aggregateId(),
                message.eventType(),
                message.payload()
        );
        repository.save(entity);
    }

    @Override
    public List<OutboxMessage> pollUndispatched(int batchSize) {
        return repository.findUndispatched(batchSize).stream()
                .map(e -> new OutboxMessage(e.getId(), e.getAggregateType(), e.getAggregateId(),
                        e.getEventType(), e.getPayload(), e.getCreatedAt(), e.getAttemptCount()))
                .toList();
    }

    @Override
    public void markDispatched(String messageId) {
        repository.findById(messageId).ifPresent(e -> {
            e.markDispatched();
            repository.save(e);
        });
    }

    @Override
    public void markFailed(String messageId, String errorMessage) {
        repository.findById(messageId).ifPresent(e -> {
            e.recordFailure(errorMessage);
            repository.save(e);
        });
    }

    @Override
    public void markDead(String messageId, String reason) {
        repository.findById(messageId).ifPresent(e -> {
            e.markDead(reason);
            repository.save(e);
        });
    }
}
