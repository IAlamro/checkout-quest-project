package com.checkout.ledger.infrastructure.messaging.inbound;

import com.checkout.ledger.application.usecase.postpaymentfailed.PostPaymentFailedCommand;
import com.checkout.ledger.application.usecase.postpaymentfailed.PostPaymentFailedHandler;
import com.checkout.payment.application.integration.event.PaymentFailedIntegrationEvent;
import com.checkout.shared.domain.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFailedLedgerListener {

    private final PostPaymentFailedHandler handler;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(PaymentFailedIntegrationEvent event) {
        handler.handle(new PostPaymentFailedCommand(event.paymentId(), Money.of(event.amount(), event.currency())));
    }
}
