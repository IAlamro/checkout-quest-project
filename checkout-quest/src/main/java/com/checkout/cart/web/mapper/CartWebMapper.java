package com.checkout.cart.web.mapper;

import com.checkout.cart.infrastructure.projection.CartItemReadModelEntity;
import com.checkout.cart.infrastructure.projection.CartReadModelEntity;
import com.checkout.cart.web.dto.CartItemResponse;
import com.checkout.cart.web.dto.CartResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartWebMapper {

    CartResponse toResponse(CartReadModelEntity entity);

    CartItemResponse toResponse(CartItemReadModelEntity entity);
}
