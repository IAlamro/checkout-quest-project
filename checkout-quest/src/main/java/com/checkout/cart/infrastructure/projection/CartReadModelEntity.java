package com.checkout.cart.infrastructure.projection;

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
@Table(name = "cart_read_model")
public class CartReadModelEntity {

    @Id
    @Column(name = "cart_id")
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

    @Column(name = "locked_at")
    private Instant lockedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItemReadModelEntity> items = new ArrayList<>();

    public CartReadModelEntity(String cartId, Instant createdAt) {
        this.cartId = cartId;
        this.status = "OPEN";
        this.totalAmount = BigDecimal.ZERO;
        this.currency = "USD";
        this.version = 1;
        this.createdAt = createdAt;
    }

    public void addItem(CartItemReadModelEntity item) {
        items.add(item);
        recalculateTotal();
        version++;
    }

    public void lock(Instant lockedAt) {
        this.status = "LOCKED";
        this.lockedAt = lockedAt;
        version++;
    }

    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(i -> i.getUnitPriceAmount().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
