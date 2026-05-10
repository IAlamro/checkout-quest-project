package com.checkout.order.application.usecase.checkoutcart;

import com.checkout.cart.application.api.CartFacade;
import com.checkout.cart.application.api.CartSnapshot;
import com.checkout.order.application.integration.event.OrderCheckedOutIntegrationEvent;
import com.checkout.order.application.mapper.OrderApplicationMapper;
import com.checkout.order.domain.Order;
import com.checkout.order.domain.OrderId;
import com.checkout.order.domain.OrderItem;
import com.checkout.order.infrastructure.OrderAggregateRepository;
import com.checkout.shared.cqrs.CommandHandler;
import com.checkout.shared.domain.Money;
import com.checkout.shared.outbox.OutboxMessage;
import com.checkout.shared.outbox.OutboxStore;
import com.checkout.shared.util.JsonMapper;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutCartHandler implements CommandHandler<CheckoutCartCommand, String> {

    private final CartFacade cartFacade;
    private final OrderAggregateRepository orderRepository;
    private final OutboxStore outboxStore;
    private final JsonMapper jsonMapper;
    private final OrderApplicationMapper orderApplicationMapper;

    @Override
    @Transactional
    public String handle(CheckoutCartCommand command) {
        CartSnapshot cart = cartFacade.getSnapshot(command.cartId());

        if (cart.items().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        cartFacade.lock(command.cartId());

        List<OrderItem> orderItems = cart.items().stream()
                .map(orderApplicationMapper::toOrderItem)
                .toList();

        OrderId orderId = new OrderId(UuidV7.generateAsString());
        Money total = cart.totalAmount();
        Order order = Order.checkout(orderId, command.cartId(), orderItems, total);
        orderRepository.save(order);

        OrderCheckedOutIntegrationEvent integrationEvent = new OrderCheckedOutIntegrationEvent(
                UuidV7.generateAsString(), orderId.value(), command.cartId(),
                total.amount(), total.currency().getCurrencyCode(), Instant.now()
        );
        outboxStore.enqueue(new OutboxMessage(
                UuidV7.generateAsString(), "order", orderId.value(),
                integrationEvent.eventType(), jsonMapper.serialize(integrationEvent), Instant.now(), 0
        ));

        return orderId.value();
    }
}
