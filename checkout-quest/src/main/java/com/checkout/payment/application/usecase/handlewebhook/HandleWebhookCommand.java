package com.checkout.payment.application.usecase.handlewebhook;

import com.checkout.payment.domain.WebhookType;
import com.checkout.shared.cqrs.Command;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HandleWebhookCommand implements Command {
    private final String eventId;
    private final String providerRef;
    private final WebhookType type;
    private final String reason;
}
