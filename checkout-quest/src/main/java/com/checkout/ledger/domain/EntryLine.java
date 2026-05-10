package com.checkout.ledger.domain;

import com.checkout.shared.domain.Money;

public record EntryLine(AccountCode accountCode, Money amount, Side side, String description) {
    public EntryLine {
        if (accountCode == null) throw new IllegalArgumentException("accountCode required");
        if (amount == null) throw new IllegalArgumentException("amount required");
        if (side == null) throw new IllegalArgumentException("side required");
    }
}
