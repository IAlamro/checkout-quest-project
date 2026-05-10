package com.checkout.shared.infrastructure.eventsourcing;

import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.eventsourcing.EventStore;
import com.checkout.shared.eventsourcing.EventStream;
import com.checkout.shared.eventsourcing.StoredEvent;
import com.checkout.shared.util.JsonMapper;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaEventStore implements EventStore {

    private final EventJpaRepository repository;
    private final JsonMapper jsonMapper;

    @Override
    public EventStream load(String streamId, String streamType) {
        List<EventEntity> entities = repository.findByStreamIdAndStreamTypeOrderByVersionAsc(streamId, streamType);
        List<StoredEvent> events = entities.stream().map(this::toStoredEvent).toList();
        long currentVersion = entities.isEmpty() ? 0 : entities.getLast().getVersion();
        return new EventStream(streamId, streamType, events, currentVersion);
    }

    @Override
    public void append(String streamId, String streamType, long expectedVersion, List<DomainEvent> events) {
        long nextVersion = expectedVersion + 1;
        try {
            for (DomainEvent event : events) {
                EventEntity entity = new EventEntity(
                        UuidV7.generateAsString(),
                        streamId,
                        streamType,
                        nextVersion++,
                        event.eventType(),
                        jsonMapper.serialize(event),
                        event.occurredAt()
                );
                repository.save(entity);
            }
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConcurrencyException(streamId, expectedVersion);
        }
    }

    private StoredEvent toStoredEvent(EventEntity e) {
        return new StoredEvent(e.getEventId(), e.getStreamId(), e.getStreamType(),
                e.getVersion(), e.getEventType(), e.getPayload(), e.getOccurredAt(), e.getRecordedAt());
    }
}
