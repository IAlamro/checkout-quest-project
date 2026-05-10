package com.checkout.order.web.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        int quantity,
        BigDecimal unitPriceAmount,
        String unitPriceCurrency
) {}
