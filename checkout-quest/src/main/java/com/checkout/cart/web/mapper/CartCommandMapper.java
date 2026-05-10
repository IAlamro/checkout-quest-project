package com.checkout.cart.web.mapper;

import com.checkout.cart.application.usecase.additem.AddItemCommand;
import com.checkout.cart.web.dto.AddItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartCommandMapper {

    @Mapping(source = "cartId", target = "cartId")
    @Mapping(source = "request.productId", target = "productId")
    @Mapping(source = "request.quantity", target = "quantity")
    @Mapping(source = "request.unitPrice", target = "unitPrice")
    @Mapping(source = "request.currency", target = "currency")
    AddItemCommand toCommand(AddItemRequest request, String cartId);
}
