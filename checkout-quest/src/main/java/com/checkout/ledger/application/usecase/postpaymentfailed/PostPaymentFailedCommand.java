package com.checkout.ledger.application.usecase.postpaymentfailed;

import com.checkout.shared.cqrs.Command;
import com.checkout.shared.domain.Money;

public record PostPaymentFailedCommand(String paymentId, Money amount) implements Command {}
