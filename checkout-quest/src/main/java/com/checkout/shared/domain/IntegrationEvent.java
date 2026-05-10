package com.checkout.shared.domain;

import java.time.Instant;

public interface IntegrationEvent {
    String eventId();
    Instant occurredAt();

    default String eventType() {
        return this.getClass().getName();
    }
}
