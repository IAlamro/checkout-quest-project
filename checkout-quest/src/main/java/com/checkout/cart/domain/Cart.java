package com.checkout.cart.domain;

import com.checkout.cart.domain.event.CartCreated;
import com.checkout.cart.domain.event.CartLocked;
import com.checkout.cart.domain.event.ItemAdded;
import com.checkout.shared.domain.AggregateRoot;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart extends AggregateRoot {

    private CartId id;
    private CartStatus status;
    private final List<CartItem> items = new ArrayList<>();

    private Cart() {}

    /** Used exclusively by CartAggregateRepository to create an empty shell for event replay. */
    public static Cart reconstitute() { return new Cart(); }

    public static Cart create(CartId cartId) {
        Cart cart = new Cart();
        cart.record(new CartCreated(cartId, Instant.now()));
        return cart;
    }

    public void addItem(CartItem item) {
        if (status == CartStatus.LOCKED) {
            throw new CartLockedException(id);
        }
        record(new ItemAdded(id, item, Instant.now()));
    }

    public void lock() {
        if (status == CartStatus.LOCKED) return;
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot lock an empty cart");
        }
        record(new CartLocked(id, Instant.now()));
    }

    public Money totalAmount() {
        return items.stream()
                .map(CartItem::lineTotal)
                .reduce(Money.usd(0), Money::add);
    }

    @Override
    protected void applyEvent(DomainEvent event) {
        switch (event) {
            case CartCreated e -> {
                this.id = e.cartId();
                this.status = CartStatus.OPEN;
            }
            case ItemAdded e -> this.items.add(e.item());
            case CartLocked e -> this.status = CartStatus.LOCKED;
            default -> throw new IllegalArgumentException("Unknown Cart event: " + event.getClass());
        }
    }

    public CartId getId() { return id; }
    public CartStatus getStatus() { return status; }
    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }

    public static class CartLockedException extends RuntimeException {
        public CartLockedException(CartId cartId) {
            super("Cart %s is locked and cannot be modified".formatted(cartId.value()));
        }
    }
}
