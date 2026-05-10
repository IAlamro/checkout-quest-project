package com.checkout.shared.infrastructure.config;

public record ErrorResponse(int status, String error, String message, String timestamp) {}
