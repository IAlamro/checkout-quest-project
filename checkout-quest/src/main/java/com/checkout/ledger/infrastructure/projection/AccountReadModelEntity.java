package com.checkout.ledger.infrastructure.projection;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ledger_account")
public class AccountReadModelEntity {

    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "normal_balance", nullable = false)
    private String normalBalance;

    @Column(name = "balance_amount", nullable = false)
    private BigDecimal balanceAmount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "version", nullable = false)
    private long version;

    public void applyDebit(BigDecimal amount) {
        // Debit increases DEBIT-normal accounts (ASSET, EXPENSE), decreases CREDIT-normal (LIABILITY, REVENUE)
        if ("DEBIT".equals(normalBalance)) {
            balanceAmount = balanceAmount.add(amount);
        } else {
            balanceAmount = balanceAmount.subtract(amount);
        }
        version++;
    }

    public void applyCredit(BigDecimal amount) {
        // Credit increases CREDIT-normal accounts (LIABILITY, REVENUE), decreases DEBIT-normal (ASSET, EXPENSE)
        if ("CREDIT".equals(normalBalance)) {
            balanceAmount = balanceAmount.add(amount);
        } else {
            balanceAmount = balanceAmount.subtract(amount);
        }
        version++;
    }

}
