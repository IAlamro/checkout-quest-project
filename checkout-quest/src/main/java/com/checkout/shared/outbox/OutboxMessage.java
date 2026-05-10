package com.checkout.shared.outbox;

import java.time.Instant;

public record OutboxMessage(
        String id,
        String aggregateType,
        String aggregateId,
        String eventType,
        String payload,
        Instant createdAt,
        int attemptCount
) {}
