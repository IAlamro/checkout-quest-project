package com.checkout.shared.domain;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();

    default String eventType() {
        return this.getClass().getName();
    }
}
