package com.checkout.cart.application.usecase.getcart;

import com.checkout.cart.infrastructure.projection.CartJpaRepository;
import com.checkout.cart.infrastructure.projection.CartReadModelEntity;
import com.checkout.shared.cqrs.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCartHandler implements QueryHandler<GetCartQuery, CartReadModelEntity> {

    private final CartJpaRepository cartJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public CartReadModelEntity handle(GetCartQuery query) {
        return cartJpaRepository.findById(query.cartId())
                .orElseThrow(() -> new CartNotFoundException(query.cartId()));
    }

    public static class CartNotFoundException extends RuntimeException {
        public CartNotFoundException(String cartId) {
            super("Cart not found: " + cartId);
        }
    }
}
