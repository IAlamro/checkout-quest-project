package com.checkout.cart.web.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        String productId,
        int quantity,
        BigDecimal unitPriceAmount,
        String unitPriceCurrency
) {}
