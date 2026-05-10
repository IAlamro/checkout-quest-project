package com.checkout.ledger.infrastructure.messaging.inbound;

import com.checkout.ledger.application.usecase.postpaymentconfirmed.PostPaymentConfirmedCommand;
import com.checkout.ledger.application.usecase.postpaymentconfirmed.PostPaymentConfirmedHandler;
import com.checkout.payment.application.integration.event.PaymentConfirmedIntegrationEvent;
import com.checkout.shared.domain.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentConfirmedLedgerListener {

    private final PostPaymentConfirmedHandler handler;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(PaymentConfirmedIntegrationEvent event) {
        handler.handle(new PostPaymentConfirmedCommand(
                event.paymentId(),
                Money.of(event.amount(), event.currency())
        ));
    }
}
