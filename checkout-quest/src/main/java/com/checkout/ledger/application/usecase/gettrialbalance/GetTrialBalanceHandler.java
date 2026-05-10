package com.checkout.ledger.application.usecase.gettrialbalance;

import com.checkout.ledger.infrastructure.projection.AccountJpaRepository;
import com.checkout.ledger.infrastructure.projection.AccountReadModelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTrialBalanceHandler {

    private final AccountJpaRepository accountJpaRepository;

    @Transactional(readOnly = true)
    public TrialBalanceResult handle() {
        List<AccountReadModelEntity> accounts = accountJpaRepository.findAll();

        BigDecimal totalDebits = accounts.stream()
                .filter(a -> "DEBIT".equals(a.getNormalBalance()))
                .map(AccountReadModelEntity::getBalanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = accounts.stream()
                .filter(a -> "CREDIT".equals(a.getNormalBalance()))
                .map(AccountReadModelEntity::getBalanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TrialBalanceResult(accounts, totalDebits, totalCredits,
                totalDebits.compareTo(totalCredits) == 0);
    }
}
