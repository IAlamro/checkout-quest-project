package com.checkout.order.application.usecase.getorder;

import com.checkout.order.infrastructure.projection.OrderJpaRepository;
import com.checkout.order.infrastructure.projection.OrderReadModelEntity;
import com.checkout.shared.cqrs.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrderHandler implements QueryHandler<GetOrderQuery, OrderReadModelEntity> {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderReadModelEntity handle(GetOrderQuery query) {
        return orderJpaRepository.findById(query.orderId())
                .orElseThrow(() -> new OrderNotFoundException(query.orderId()));
    }

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String orderId) {
            super("Order not found: " + orderId);
        }
    }
}
