package com.checkout.ledger.domain;

import java.math.BigDecimal;

public enum NormalBalance {
    DEBIT, CREDIT;

    /**
     * Returns the impact on account balance: positive = increase, negative = decrease.
     */
    public BigDecimal balanceImpact(Side side, BigDecimal amount) {
        boolean increasing = (this == DEBIT && side == Side.DEBIT)
                || (this == CREDIT && side == Side.CREDIT);
        return increasing ? amount : amount.negate();
    }
}
