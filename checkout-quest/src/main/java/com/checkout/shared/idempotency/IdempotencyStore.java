package com.checkout.shared.idempotency;

import java.util.Optional;

public interface IdempotencyStore {

    /**
     * Reserves a key. Returns empty if successfully reserved (new key).
     * Returns the existing record if the key already exists.
     */
    Optional<IdempotencyRecord> reserve(String key, String endpoint, String fingerprint);

    void complete(String key, int statusCode, String responseBody);
}
