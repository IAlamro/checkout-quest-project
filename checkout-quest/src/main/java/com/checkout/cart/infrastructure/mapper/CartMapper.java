package com.checkout.cart.infrastructure.mapper;

import com.checkout.cart.application.api.CartItemSnapshot;
import com.checkout.cart.domain.CartId;
import com.checkout.cart.domain.CartItem;
import com.checkout.cart.infrastructure.projection.CartItemReadModelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "id", source = "item.itemId")
    @Mapping(target = "cartId", source = "cartId.value")
    @Mapping(target = "productId", source = "item.product.productId")
    @Mapping(target = "quantity", source = "item.quantity")
    @Mapping(target = "unitPriceAmount", source = "item.product.unitPrice.amount")
    @Mapping(target = "unitPriceCurrency", expression = "java(item.product().unitPrice().currency().getCurrencyCode())")
    CartItemReadModelEntity toReadModel(CartId cartId, CartItem item);

    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "unitPrice", source = "product.unitPrice")
    CartItemSnapshot toSnapshot(CartItem item);
}
