package com.checkout.shared.domain;

/**
 * Thrown when a business invariant is violated. The outbox relay dead-letters
 * the message immediately - retrying cannot resolve a domain rule violation.
 */
public abstract class NonRetryableException extends RuntimeException {
    protected NonRetryableException(String message) {
        super(message);
    }
}
