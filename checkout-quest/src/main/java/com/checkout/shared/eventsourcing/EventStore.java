package com.checkout.shared.eventsourcing;

import com.checkout.shared.domain.DomainEvent;

import java.util.List;

public interface EventStore {

    EventStream load(String streamId, String streamType);

    /**
     * Appends events to a stream, enforcing optimistic concurrency via expectedVersion.
     * The first new event gets version expectedVersion+1.
     * Throws ConcurrencyException if another writer has already written at the expected position.
     */
    void append(String streamId, String streamType, long expectedVersion, List<DomainEvent> events);

    class ConcurrencyException extends RuntimeException {
        public ConcurrencyException(String streamId, long expectedVersion) {
            super("Concurrency conflict on stream %s at version %d".formatted(streamId, expectedVersion));
        }
    }

    class StreamNotFoundException extends RuntimeException {
        public StreamNotFoundException(String streamId) {
            super("Event stream not found: " + streamId);
        }
    }
}
