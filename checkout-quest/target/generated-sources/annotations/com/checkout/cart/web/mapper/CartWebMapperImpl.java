package com.checkout.cart.web.mapper;

import com.checkout.cart.infrastructure.projection.CartItemReadModelEntity;
import com.checkout.cart.infrastructure.projection.CartReadModelEntity;
import com.checkout.cart.web.dto.CartItemResponse;
import com.checkout.cart.web.dto.CartResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class CartWebMapperImpl implements CartWebMapper {

    @Override
    public CartResponse toResponse(CartReadModelEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String cartId = null;
        String status = null;
        BigDecimal totalAmount = null;
        String currency = null;
        Instant createdAt = null;
        Instant lockedAt = null;
        List<CartItemResponse> items = null;

        cartId = entity.getCartId();
        status = entity.getStatus();
        totalAmount = entity.getTotalAmount();
        currency = entity.getCurrency();
        createdAt = entity.getCreatedAt();
        lockedAt = entity.getLockedAt();
        items = cartItemReadModelEntityListToCartItemResponseList( entity.getItems() );

        CartResponse cartResponse = new CartResponse( cartId, status, totalAmount, currency, createdAt, lockedAt, items );

        return cartResponse;
    }

    @Override
    public CartItemResponse toResponse(CartItemReadModelEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String productId = null;
        int quantity = 0;
        BigDecimal unitPriceAmount = null;
        String unitPriceCurrency = null;

        productId = entity.getProductId();
        quantity = entity.getQuantity();
        unitPriceAmount = entity.getUnitPriceAmount();
        unitPriceCurrency = entity.getUnitPriceCurrency();

        CartItemResponse cartItemResponse = new CartItemResponse( productId, quantity, unitPriceAmount, unitPriceCurrency );

        return cartItemResponse;
    }

    protected List<CartItemResponse> cartItemReadModelEntityListToCartItemResponseList(List<CartItemReadModelEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemResponse> list1 = new ArrayList<CartItemResponse>( list.size() );
        for ( CartItemReadModelEntity cartItemReadModelEntity : list ) {
            list1.add( toResponse( cartItemReadModelEntity ) );
        }

        return list1;
    }
}
