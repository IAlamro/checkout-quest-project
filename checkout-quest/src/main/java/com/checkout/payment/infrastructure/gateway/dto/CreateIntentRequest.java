package com.checkout.payment.infrastructure.gateway.dto;

public record CreateIntentRequest(String paymentId, String amount, String currency) {}
