package com.checkout.ledger.web.dto;

import java.math.BigDecimal;

public record EntryLineResponse(
        String accountCode,
        BigDecimal amount,
        String currency,
        String side,
        String description
) {}
