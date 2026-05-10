package com.checkout.payment.infrastructure.projection;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "processed_webhook_events")
public class ProcessedWebhookEventEntity {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
