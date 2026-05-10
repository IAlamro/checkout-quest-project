package com.checkout.payment.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Webhook payload from the payment provider")
public record WebhookRequest(
        @Schema(description = "Globally unique event identifier - used for deduplication, replaying the same eventId is a safe no-op", example = "evt-conf-abc123")
        @NotBlank String eventId,

        @Schema(description = "Provider's reference for the payment - returned when the intent was created", example = "prov-ref-abc123")
        @NotBlank String providerRef,

        @Schema(description = "Webhook event type", example = "CONFIRMED", allowableValues = {"AUTHORIZED", "CONFIRMED", "FAILED"})
        @NotNull String type,

        @Schema(description = "Human-readable reason, used mainly for FAILED webhooks", example = "Insufficient funds", nullable = true)
        String reason
) {}
