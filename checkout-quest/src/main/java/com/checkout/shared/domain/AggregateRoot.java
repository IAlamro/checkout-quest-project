package com.checkout.shared.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot {

    private long baseVersion = 0;
    private long version = 0;
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    protected void record(DomainEvent event) {
        uncommittedEvents.add(event);
        applyEvent(event);
        version++;
    }

    protected abstract void applyEvent(DomainEvent event);

    /** Called by AggregateRepository during event replay - not by aggregates directly. */
    public void replayEvent(DomainEvent event, long eventVersion) {
        applyEvent(event);
        this.baseVersion = eventVersion;
        this.version = eventVersion;
    }

    public long getBaseVersion() {
        return baseVersion;
    }

    public long getVersion() {
        return version;
    }

    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    public void markEventsCommitted() {
        this.baseVersion = this.version;
        uncommittedEvents.clear();
    }
}
