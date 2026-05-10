package com.checkout.payment.application.usecase.getpayment;

import com.checkout.payment.infrastructure.projection.PaymentReadModelEntity;
import com.checkout.shared.cqrs.Query;

public record GetPaymentQuery(String orderId) implements Query<PaymentReadModelEntity> {}
