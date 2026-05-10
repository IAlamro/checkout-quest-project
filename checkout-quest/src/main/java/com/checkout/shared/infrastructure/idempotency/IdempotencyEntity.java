package com.checkout.shared.infrastructure.idempotency;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "idempotency_records")
public class IdempotencyEntity {

    @Id
    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String key;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "fingerprint", nullable = false)
    private String fingerprint;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "status_code")
    private Integer statusCode;

    @Lob
    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public IdempotencyEntity(String key, String endpoint, String fingerprint) {
        this.key = key;
        this.endpoint = endpoint;
        this.fingerprint = fingerprint;
        this.status = "IN_PROGRESS";
        this.createdAt = Instant.now();
    }

    public void complete(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.status = "COMPLETED";
        this.completedAt = Instant.now();
    }

    public boolean isCompleted() { return "COMPLETED".equals(status); }
}
