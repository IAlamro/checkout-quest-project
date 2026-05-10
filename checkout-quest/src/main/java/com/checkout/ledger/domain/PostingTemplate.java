package com.checkout.ledger.domain;

import java.util.List;

public record PostingTemplate(ReferenceType referenceType, List<TemplateLine> lines) {

    public record TemplateLine(AccountCode accountCode, Side side, String description) {}
}
