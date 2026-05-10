package com.checkout.payment.infrastructure.projection;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "payment_read_model")
public class PaymentReadModelEntity {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "provider_ref")
    private String providerRef;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public PaymentReadModelEntity(String paymentId, String orderId, BigDecimal amount,
                                   String currency, String idempotencyKey) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.status = "INITIATED";
        this.amount = amount;
        this.currency = currency;
        this.idempotencyKey = idempotencyKey;
        this.version = 1;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
        this.version++;
    }

    public void setProviderRef(String providerRef) {
        this.providerRef = providerRef;
        this.updatedAt = Instant.now();
    }
}
