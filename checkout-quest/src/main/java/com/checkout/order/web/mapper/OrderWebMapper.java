package com.checkout.order.web.mapper;

import com.checkout.order.infrastructure.projection.OrderItemReadModelEntity;
import com.checkout.order.infrastructure.projection.OrderReadModelEntity;
import com.checkout.order.web.dto.OrderItemResponse;
import com.checkout.order.web.dto.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderWebMapper {

    OrderResponse toResponse(OrderReadModelEntity entity);

    OrderItemResponse toResponse(OrderItemReadModelEntity entity);
}
