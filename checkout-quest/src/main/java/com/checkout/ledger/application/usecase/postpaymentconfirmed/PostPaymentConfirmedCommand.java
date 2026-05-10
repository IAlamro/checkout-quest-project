package com.checkout.ledger.application.usecase.postpaymentconfirmed;

import com.checkout.shared.cqrs.Command;
import com.checkout.shared.domain.Money;

public record PostPaymentConfirmedCommand(String paymentId, Money amount) implements Command {}
