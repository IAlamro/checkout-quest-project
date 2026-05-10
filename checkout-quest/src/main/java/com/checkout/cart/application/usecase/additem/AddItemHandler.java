package com.checkout.cart.application.usecase.additem;

import com.checkout.cart.domain.Cart;
import com.checkout.cart.domain.CartItem;
import com.checkout.cart.domain.ProductRef;
import com.checkout.cart.infrastructure.CartAggregateRepository;
import com.checkout.shared.cqrs.CommandHandler;
import com.checkout.shared.domain.Money;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddItemHandler implements CommandHandler<AddItemCommand, Void> {

    private final CartAggregateRepository cartRepository;

    @Override
    @Transactional
    public Void handle(AddItemCommand command) {
        Cart cart = cartRepository.load(command.getCartId());
        ProductRef product = new ProductRef(command.getProductId(), Money.of(command.getUnitPrice(), command.getCurrency()));
        CartItem item = new CartItem(UuidV7.generateAsString(), product, command.getQuantity());
        cart.addItem(item);
        cartRepository.save(cart);
        return null;
    }
}
