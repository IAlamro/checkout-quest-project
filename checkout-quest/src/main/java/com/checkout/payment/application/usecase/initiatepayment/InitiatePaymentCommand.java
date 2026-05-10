package com.checkout.payment.application.usecase.initiatepayment;

import com.checkout.shared.cqrs.Command;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class InitiatePaymentCommand implements Command {
    private final String orderId;
    private final BigDecimal amount;
    private final String currency;
    private final String idempotencyKey;
}
