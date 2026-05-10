package com.checkout.order.application.usecase.getorder;

import com.checkout.order.infrastructure.projection.OrderReadModelEntity;
import com.checkout.shared.cqrs.Query;

public record GetOrderQuery(String orderId) implements Query<OrderReadModelEntity> {}
