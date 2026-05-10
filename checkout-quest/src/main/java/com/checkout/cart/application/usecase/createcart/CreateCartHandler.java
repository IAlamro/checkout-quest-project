package com.checkout.cart.application.usecase.createcart;

import com.checkout.cart.domain.Cart;
import com.checkout.cart.domain.CartId;
import com.checkout.cart.infrastructure.CartAggregateRepository;
import com.checkout.shared.cqrs.CommandHandler;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCartHandler implements CommandHandler<CreateCartCommand, String> {

    private final CartAggregateRepository cartRepository;

    @Override
    @Transactional
    public String handle(CreateCartCommand command) {
        CartId cartId = new CartId(UuidV7.generateAsString());
        Cart cart = Cart.create(cartId);
        cartRepository.save(cart);
        return cartId.value();
    }
}
