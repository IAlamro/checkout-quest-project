package com.checkout.order.infrastructure.messaging.inbound;

import com.checkout.order.infrastructure.OrderAggregateRepository;
import com.checkout.payment.application.integration.event.PaymentInitiatedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentInitiatedOrderListener {

    private final OrderAggregateRepository orderRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(PaymentInitiatedIntegrationEvent event) {
        var order = orderRepository.load(event.orderId());
        order.requestPayment(event.paymentId());
        orderRepository.save(order);
    }
}
