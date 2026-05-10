package com.checkout.payment.web;

import com.checkout.payment.application.usecase.handlewebhook.HandleWebhookHandler;
import com.checkout.payment.web.dto.WebhookRequest;
import com.checkout.payment.web.mapper.PaymentCommandMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Payment provider callbacks - receives state transitions from the external payment provider")
public class WebhookController {

    private final HandleWebhookHandler handleWebhookHandler;
    private final PaymentCommandMapper paymentCommandMapper;

    @PostMapping("/webhook")
    @Operation(
            summary = "Handle a provider webhook",
            description = "Called by the payment provider to push state transitions (AUTHORIZED, CONFIRMED, FAILED) back into the system. " +
                    "Deduplication is handled by eventId - replaying the same eventId is always a safe no-op and returns 200. " +
                    "You can also call this manually from Postman to simulate provider callbacks."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook accepted (including idempotent replays)"),
            @ApiResponse(responseCode = "400", description = "Validation error - check eventId, providerRef, and type fields", content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid state transition for this payment", content = @Content)
    })
    public ResponseEntity<Void> handleWebhook(@Valid @RequestBody WebhookRequest request) {
        handleWebhookHandler.handle(paymentCommandMapper.toCommand(request));
        return ResponseEntity.ok().build();
    }
}
