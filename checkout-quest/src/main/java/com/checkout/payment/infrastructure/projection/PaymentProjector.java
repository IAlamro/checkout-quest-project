package com.checkout.payment.infrastructure.projection;

import com.checkout.payment.domain.event.PaymentAuthorized;
import com.checkout.payment.domain.event.PaymentConfirmed;
import com.checkout.payment.domain.event.PaymentFailed;
import com.checkout.payment.domain.event.PaymentInitiated;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentProjector {

    private final PaymentJpaRepository paymentJpaRepository;

    @EventListener
    @Transactional
    public void on(PaymentInitiated event) {
        paymentJpaRepository.save(new PaymentReadModelEntity(
                event.paymentId().value(),
                event.orderId(),
                event.amount().amount(),
                event.amount().currency().getCurrencyCode(),
                event.idempotencyKey()
        ));
    }

    @EventListener
    @Transactional
    public void on(PaymentAuthorized event) {
        paymentJpaRepository.findById(event.paymentId().value()).ifPresent(p -> {
            p.updateStatus("AUTHORIZED");
            p.setProviderRef(event.providerRef().externalId());
            paymentJpaRepository.save(p);
        });
    }

    @EventListener
    @Transactional
    public void on(PaymentConfirmed event) {
        paymentJpaRepository.findById(event.paymentId().value()).ifPresent(p -> {
            p.updateStatus("CONFIRMED");
            paymentJpaRepository.save(p);
        });
    }

    @EventListener
    @Transactional
    public void on(PaymentFailed event) {
        paymentJpaRepository.findById(event.paymentId().value()).ifPresent(p -> {
            p.updateStatus("FAILED");
            paymentJpaRepository.save(p);
        });
    }
}
