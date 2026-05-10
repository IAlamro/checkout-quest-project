package com.checkout.shared.eventsourcing;

import com.checkout.shared.domain.AggregateRoot;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.util.JsonMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRepository<T extends AggregateRoot> {

    protected final EventStore eventStore;
    private final JsonMapper jsonMapper;
    private final ApplicationEventPublisher eventPublisher;

    protected AggregateRepository(EventStore eventStore, JsonMapper jsonMapper,
                                   ApplicationEventPublisher eventPublisher) {
        this.eventStore = eventStore;
        this.jsonMapper = jsonMapper;
        this.eventPublisher = eventPublisher;
    }

    protected abstract T createEmpty();

    protected abstract String streamType();

    protected abstract String streamId(T aggregate);

    public T load(String id) {
        EventStream stream = eventStore.load(id, streamType());
        if (stream.isEmpty()) {
            throw new EventStore.StreamNotFoundException(id);
        }
        T aggregate = createEmpty();
        for (StoredEvent storedEvent : stream.events()) {
            DomainEvent domainEvent = jsonMapper.deserialize(storedEvent.payload(), storedEvent.eventType());
            aggregate.replayEvent(domainEvent, storedEvent.version());
        }
        return aggregate;
    }

    public void save(T aggregate) {
        List<DomainEvent> uncommitted = new ArrayList<>(aggregate.getUncommittedEvents());
        if (uncommitted.isEmpty()) return;

        eventStore.append(
                streamId(aggregate),
                streamType(),
                aggregate.getBaseVersion(),
                uncommitted
        );
        aggregate.markEventsCommitted();

        // Publish so projectors and listeners can react within the same transaction
        uncommitted.forEach(eventPublisher::publishEvent);
    }
}
