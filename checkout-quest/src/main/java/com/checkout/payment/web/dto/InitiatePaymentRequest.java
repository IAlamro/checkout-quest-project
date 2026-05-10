package com.checkout.payment.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request body for initiating a payment")
public record InitiatePaymentRequest(
        @Schema(description = "Payment amount - must match the order total", example = "1399.97", minimum = "0.01")
        @NotNull @DecimalMin("0.01") BigDecimal amount,

        @Schema(description = "ISO 4217 currency code", example = "USD")
        @NotBlank String currency
) {}
