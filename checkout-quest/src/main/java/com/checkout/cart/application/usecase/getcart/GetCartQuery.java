package com.checkout.cart.application.usecase.getcart;

import com.checkout.shared.cqrs.Query;
import com.checkout.cart.infrastructure.projection.CartReadModelEntity;

public record GetCartQuery(String cartId) implements Query<CartReadModelEntity> {}
