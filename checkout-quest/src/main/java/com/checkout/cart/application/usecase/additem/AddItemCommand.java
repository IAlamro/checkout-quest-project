package com.checkout.cart.application.usecase.additem;

import com.checkout.shared.cqrs.Command;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AddItemCommand implements Command {
    private final String cartId;
    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final String currency;
}
