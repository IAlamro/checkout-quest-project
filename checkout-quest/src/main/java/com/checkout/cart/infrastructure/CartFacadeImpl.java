package com.checkout.cart.infrastructure;

import com.checkout.cart.application.api.CartFacade;
import com.checkout.cart.application.api.CartItemSnapshot;
import com.checkout.cart.application.api.CartSnapshot;
import com.checkout.cart.domain.Cart;
import com.checkout.cart.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartFacadeImpl implements CartFacade {

    private final CartAggregateRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public CartSnapshot getSnapshot(String cartId) {
        Cart cart = cartRepository.load(cartId);
        List<CartItemSnapshot> items = cart.getItems().stream()
                .map(cartMapper::toSnapshot)
                .toList();
        return new CartSnapshot(cart.getId().value(), cart.getStatus().name(), items, cart.totalAmount());
    }

    @Override
    @Transactional
    public void lock(String cartId) {
        Cart cart = cartRepository.load(cartId);
        cart.lock();
        cartRepository.save(cart);
    }
}
