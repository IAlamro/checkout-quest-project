package com.checkout.cart.infrastructure.projection;

import com.checkout.cart.domain.event.CartCreated;
import com.checkout.cart.domain.event.CartLocked;
import com.checkout.cart.domain.event.ItemAdded;
import com.checkout.cart.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartProjector {

    private final CartJpaRepository cartJpaRepository;
    private final CartMapper cartMapper;

    @EventListener
    @Transactional
    public void on(CartCreated event) {
        cartJpaRepository.save(new CartReadModelEntity(event.cartId().value(), event.occurredAt()));
    }

    @EventListener
    @Transactional
    public void on(ItemAdded event) {
        CartReadModelEntity cart = cartJpaRepository.findById(event.cartId().value())
                .orElseThrow(() -> new IllegalStateException("Cart read model missing: " + event.cartId().value()));
        cart.addItem(cartMapper.toReadModel(event.cartId(), event.item()));
        cartJpaRepository.save(cart);
    }

    @EventListener
    @Transactional
    public void on(CartLocked event) {
        CartReadModelEntity cart = cartJpaRepository.findById(event.cartId().value())
                .orElseThrow(() -> new IllegalStateException("Cart read model missing: " + event.cartId().value()));
        cart.lock(event.occurredAt());
        cartJpaRepository.save(cart);
    }
}
