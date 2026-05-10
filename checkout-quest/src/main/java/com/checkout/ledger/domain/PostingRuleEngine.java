package com.checkout.ledger.domain;

import java.util.List;
import java.util.Map;

/**
 * Maps each ReferenceType to its double-entry posting template.
 * Stateless, pure domain service - no framework dependencies.
 */
public class PostingRuleEngine {

    private static final Map<ReferenceType, PostingTemplate> TEMPLATES = Map.of(
            ReferenceType.PAYMENT_CONFIRMED, new PostingTemplate(
                    ReferenceType.PAYMENT_CONFIRMED,
                    List.of(
                            new PostingTemplate.TemplateLine(AccountCode.CASH, Side.DEBIT, "Cash received"),
                            new PostingTemplate.TemplateLine(AccountCode.REVENUE, Side.CREDIT, "Revenue recognized")
                    )
            ),
            ReferenceType.PAYMENT_FAILED, new PostingTemplate(
                    ReferenceType.PAYMENT_FAILED,
                    List.of(
                            new PostingTemplate.TemplateLine(AccountCode.TRANSACTION_EXPENSE, Side.DEBIT, "Failed transaction expense"),
                            new PostingTemplate.TemplateLine(AccountCode.CASH, Side.CREDIT, "Cash reversed for failed payment")
                    )
            )
    );

    private PostingRuleEngine() {}

    public static PostingTemplate templateFor(ReferenceType referenceType) {
        PostingTemplate template = TEMPLATES.get(referenceType);
        if (template == null) {
            throw new IllegalArgumentException("No posting template for reference type: " + referenceType);
        }
        return template;
    }
}
