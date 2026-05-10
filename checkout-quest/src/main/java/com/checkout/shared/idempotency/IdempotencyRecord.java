package com.checkout.shared.idempotency;

import java.time.Instant;

public record IdempotencyRecord(
        String key,
        String endpoint,
        String fingerprint,
        RecordStatus status,
        Integer statusCode,
        String responseBody,
        Instant createdAt,
        Instant completedAt
) {
    public enum RecordStatus { IN_PROGRESS, COMPLETED }

    public boolean isCompleted() {
        return status == RecordStatus.COMPLETED;
    }
}
