package com.checkout.order.infrastructure.mapper;

import com.checkout.order.domain.OrderItem;
import com.checkout.order.infrastructure.projection.OrderItemReadModelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "itemId")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "productId", source = "item.productId")
    @Mapping(target = "quantity", source = "item.quantity")
    @Mapping(target = "unitPriceAmount", source = "item.unitPrice.amount")
    @Mapping(target = "unitPriceCurrency", expression = "java(item.unitPrice().currency().getCurrencyCode())")
    OrderItemReadModelEntity toReadModel(String itemId, String orderId, OrderItem item);
}
