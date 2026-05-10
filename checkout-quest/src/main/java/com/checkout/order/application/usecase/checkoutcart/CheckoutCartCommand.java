package com.checkout.order.application.usecase.checkoutcart;

import com.checkout.shared.cqrs.Command;

public record CheckoutCartCommand(String cartId) implements Command {}
