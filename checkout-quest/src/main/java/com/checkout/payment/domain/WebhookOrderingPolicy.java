package com.checkout.payment.domain;

import java.util.Map;
import java.util.Set;

/**
 * Table-driven policy enforcing valid webhook-triggered transitions on a Payment.
 */
final class WebhookOrderingPolicy {

    private static final Map<PaymentStatus, Set<WebhookType>> ALLOWED = Map.of(
            PaymentStatus.INITIATED,   Set.of(WebhookType.AUTHORIZED, WebhookType.FAILED),
            PaymentStatus.AUTHORIZED,  Set.of(WebhookType.CONFIRMED, WebhookType.FAILED)
    );

    private WebhookOrderingPolicy() {}

    static boolean isLegalTransition(PaymentStatus current, WebhookType webhookType) {
        return ALLOWED.getOrDefault(current, Set.of()).contains(webhookType);
    }
}
