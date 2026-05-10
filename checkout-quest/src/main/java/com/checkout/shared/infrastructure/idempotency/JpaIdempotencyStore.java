package com.checkout.shared.infrastructure.idempotency;

import com.checkout.shared.idempotency.IdempotencyRecord;
import com.checkout.shared.idempotency.IdempotencyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaIdempotencyStore implements IdempotencyStore {

    private final IdempotencyJpaRepository repository;

    @Override
    public Optional<IdempotencyRecord> reserve(String key, String endpoint, String fingerprint) {
        Optional<IdempotencyEntity> existing = repository.findById(key);
        if (existing.isPresent()) {
            return Optional.of(toRecord(existing.get()));
        }
        try {
            repository.saveAndFlush(new IdempotencyEntity(key, endpoint, fingerprint));
            return Optional.empty();
        } catch (DataIntegrityViolationException e) {
            return repository.findById(key).map(this::toRecord);
        }
    }

    @Override
    public void complete(String key, int statusCode, String responseBody) {
        repository.findById(key).ifPresent(entity -> {
            entity.complete(statusCode, responseBody);
            repository.save(entity);
        });
    }

    private IdempotencyRecord toRecord(IdempotencyEntity entity) {
        return new IdempotencyRecord(
                entity.getKey(),
                entity.getEndpoint(),
                entity.getFingerprint(),
                entity.isCompleted() ? IdempotencyRecord.RecordStatus.COMPLETED : IdempotencyRecord.RecordStatus.IN_PROGRESS,
                entity.getStatusCode(),
                entity.getResponseBody(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}
