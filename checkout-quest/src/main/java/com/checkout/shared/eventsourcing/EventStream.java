package com.checkout.shared.eventsourcing;

import java.util.List;

public record EventStream(String streamId, String streamType, List<StoredEvent> events, long currentVersion) {

    public boolean isEmpty() {
        return events.isEmpty();
    }
}
