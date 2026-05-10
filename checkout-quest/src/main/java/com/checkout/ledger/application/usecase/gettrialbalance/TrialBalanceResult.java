package com.checkout.ledger.application.usecase.gettrialbalance;

import com.checkout.ledger.infrastructure.projection.AccountReadModelEntity;

import java.math.BigDecimal;
import java.util.List;

public record TrialBalanceResult(
        List<AccountReadModelEntity> accounts,
        BigDecimal totalDebitBalances,
        BigDecimal totalCreditBalances,
        boolean balanced
) {}
