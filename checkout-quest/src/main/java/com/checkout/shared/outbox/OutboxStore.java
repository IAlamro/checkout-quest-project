package com.checkout.shared.outbox;

import java.util.List;

public interface OutboxStore {

    void enqueue(OutboxMessage message);

    List<OutboxMessage> pollUndispatched(int batchSize);

    void markDispatched(String messageId);

    void markFailed(String messageId, String errorMessage);

    void markDead(String messageId, String reason);
}
