package com.checkout.order.infrastructure.projection;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_read_model")
public class OrderReadModelEntity {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "cart_id", nullable = false)
    private String cartId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemReadModelEntity> items = new ArrayList<>();

    public OrderReadModelEntity(String orderId, String cartId, BigDecimal totalAmount,
                                String currency, Instant createdAt) {
        this.orderId = orderId;
        this.cartId = cartId;
        this.status = "CREATED";
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.version = 1;
        this.createdAt = createdAt;
    }

    public void updateStatus(String status) {
        this.status = status;
        this.version++;
    }

    public void addItem(OrderItemReadModelEntity item) { items.add(item); }
}
