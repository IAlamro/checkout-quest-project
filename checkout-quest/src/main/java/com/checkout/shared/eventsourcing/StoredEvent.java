package com.checkout.shared.eventsourcing;

import java.time.Instant;

public record StoredEvent(
        String eventId,
        String streamId,
        String streamType,
        long version,
        String eventType,
        String payload,
        Instant occurredAt,
        Instant recordedAt
) {}
