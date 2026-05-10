package com.checkout.shared.infrastructure.eventsourcing;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "events", uniqueConstraints = {
        @UniqueConstraint(name = "ux_events_stream_version", columnNames = {"stream_id", "stream_type", "version"})
})
public class EventEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "stream_id", nullable = false, updatable = false)
    private String streamId;

    @Column(name = "stream_type", nullable = false, updatable = false)
    private String streamType;

    @Column(name = "version", nullable = false, updatable = false)
    private long version;

    @Column(name = "event_type", nullable = false, updatable = false)
    private String eventType;

    @Lob
    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private Instant recordedAt;

    public EventEntity(String eventId, String streamId, String streamType, long version,
                       String eventType, String payload, Instant occurredAt) {
        this.eventId = eventId;
        this.streamId = streamId;
        this.streamType = streamType;
        this.version = version;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.recordedAt = Instant.now();
    }
}
