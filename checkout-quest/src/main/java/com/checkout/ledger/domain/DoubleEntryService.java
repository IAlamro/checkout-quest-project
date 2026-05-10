package com.checkout.ledger.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Domain service enforcing the double-entry invariant: Σ(DEBIT) = Σ(CREDIT).
 * Throws before any persistence is attempted.
 */
public final class DoubleEntryService {

    private DoubleEntryService() {}

    public static void validate(List<EntryLine> lines) {
        if (lines == null || lines.size() < 2) {
            throw new UnbalancedEntryException("A journal entry requires at least two lines");
        }

        BigDecimal totalDebits = lines.stream()
                .filter(l -> l.side() == Side.DEBIT)
                .map(l -> l.amount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = lines.stream()
                .filter(l -> l.side() == Side.CREDIT)
                .map(l -> l.amount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new UnbalancedEntryException(
                    "Journal entry is unbalanced: debits=%s, credits=%s".formatted(totalDebits, totalCredits));
        }
    }

    public static class UnbalancedEntryException extends RuntimeException {
        public UnbalancedEntryException(String message) {
            super(message);
        }
    }
}
