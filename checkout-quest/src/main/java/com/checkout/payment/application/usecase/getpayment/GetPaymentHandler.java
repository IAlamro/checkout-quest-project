package com.checkout.payment.application.usecase.getpayment;

import com.checkout.payment.infrastructure.projection.PaymentJpaRepository;
import com.checkout.payment.infrastructure.projection.PaymentReadModelEntity;
import com.checkout.shared.cqrs.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPaymentHandler implements QueryHandler<GetPaymentQuery, PaymentReadModelEntity> {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public PaymentReadModelEntity handle(GetPaymentQuery query) {
        return paymentJpaRepository.findByOrderId(query.orderId())
                .orElseThrow(() -> new PaymentNotFoundException(query.orderId()));
    }

    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String orderId) {
            super("No payment found for order: " + orderId);
        }
    }
}
