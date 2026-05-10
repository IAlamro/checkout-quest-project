package com.checkout.shared.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Time-ordered UUID (version 7) - monotonically increasing within a millisecond,
 * globally unique, index-friendly alternative to UUIDv4.
 *
 * Layout (128 bits):
 *   [unix_ts_ms: 48][ver: 4][rand_a: 12][var: 2][rand_b: 62]
 */
public final class UuidV7 {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UuidV7() {}

    public static UUID generate() {
        long timestampMs = System.currentTimeMillis();

        long msb = (timestampMs << 16)
                | (0x7000L)                         // version 7
                | (RANDOM.nextLong() & 0x0FFFL);    // rand_a (12 bits)

        long lsb = (0x8000_0000_0000_0000L)         // variant bits
                | (RANDOM.nextLong() & 0x3FFF_FFFF_FFFF_FFFFL); // rand_b (62 bits)

        return new UUID(msb, lsb);
    }

    public static String generateAsString() {
        return generate().toString();
    }
}
