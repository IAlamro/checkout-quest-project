package com.checkout.cart.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request body for adding a product to a cart")
public record AddItemRequest(
        @Schema(description = "Product SKU or identifier", example = "prod-laptop-001")
        @NotBlank String productId,

        @Schema(description = "Number of units to add", example = "1", minimum = "1")
        @Min(1) int quantity,

        @Schema(description = "Price per unit", example = "1299.99", minimum = "0.01")
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,

        @Schema(description = "ISO 4217 currency code", example = "USD")
        @NotBlank String currency
) {}
