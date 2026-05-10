package com.checkout.ledger.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record TrialBalanceResponse(
        List<AccountResponse> accounts,
        BigDecimal totalDebitBalances,
        BigDecimal totalCreditBalances,
        boolean balanced
) {}
