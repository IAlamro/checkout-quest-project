package com.checkout.order.infrastructure.projection;

import com.checkout.order.domain.event.OrderCreated;
import com.checkout.order.domain.event.OrderPaid;
import com.checkout.order.domain.event.OrderPaymentFailed;
import com.checkout.order.domain.event.PaymentRequested;
import com.checkout.order.infrastructure.mapper.OrderMapper;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderProjector {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;

    @EventListener
    @Transactional
    public void on(OrderCreated event) {
        OrderReadModelEntity order = new OrderReadModelEntity(
                event.orderId().value(), event.cartId(),
                event.totalAmount().amount(), event.totalAmount().currency().getCurrencyCode(),
                event.occurredAt()
        );
        event.items().forEach(item -> order.addItem(
                orderMapper.toReadModel(UuidV7.generateAsString(), event.orderId().value(), item)
        ));
        orderJpaRepository.save(order);
    }

    @EventListener
    @Transactional
    public void on(PaymentRequested event) {
        orderJpaRepository.findById(event.orderId().value()).ifPresent(o -> {
            o.updateStatus("PENDING_PAYMENT");
            orderJpaRepository.save(o);
        });
    }

    @EventListener
    @Transactional
    public void on(OrderPaid event) {
        orderJpaRepository.findById(event.orderId().value()).ifPresent(o -> {
            o.updateStatus("PAID");
            orderJpaRepository.save(o);
        });
    }

    @EventListener
    @Transactional
    public void on(OrderPaymentFailed event) {
        orderJpaRepository.findById(event.orderId().value()).ifPresent(o -> {
            o.updateStatus("PAYMENT_FAILED");
            orderJpaRepository.save(o);
        });
    }
}
