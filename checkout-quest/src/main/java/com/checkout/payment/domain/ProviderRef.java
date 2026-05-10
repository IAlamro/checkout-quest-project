package com.checkout.payment.domain;

public record ProviderRef(String externalId) {
    public ProviderRef {
        if (externalId == null || externalId.isBlank())
            throw new IllegalArgumentException("ProviderRef externalId must not be blank");
    }
}
