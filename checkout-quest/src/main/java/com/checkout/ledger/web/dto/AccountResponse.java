package com.checkout.ledger.web.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String accountId,
        String code,
        String name,
        String type,
        String normalBalance,
        BigDecimal balanceAmount,
        String currency
) {}
