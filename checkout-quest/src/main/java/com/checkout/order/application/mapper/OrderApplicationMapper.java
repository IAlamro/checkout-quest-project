package com.checkout.order.application.mapper;

import com.checkout.cart.application.api.CartItemSnapshot;
import com.checkout.order.domain.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderApplicationMapper {

    OrderItem toOrderItem(CartItemSnapshot snapshot);
}
