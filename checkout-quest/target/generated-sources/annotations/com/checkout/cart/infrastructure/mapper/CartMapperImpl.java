package com.checkout.cart.infrastructure.mapper;

import com.checkout.cart.application.api.CartItemSnapshot;
import com.checkout.cart.domain.CartId;
import com.checkout.cart.domain.CartItem;
import com.checkout.cart.domain.ProductRef;
import com.checkout.cart.infrastructure.projection.CartItemReadModelEntity;
import com.checkout.shared.domain.Money;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartItemReadModelEntity toReadModel(CartId cartId, CartItem item) {
        if ( cartId == null && item == null ) {
            return null;
        }

        String cartId1 = null;
        if ( cartId != null ) {
            cartId1 = cartId.value();
        }
        String id = null;
        String productId = null;
        int quantity = 0;
        BigDecimal unitPriceAmount = null;
        if ( item != null ) {
            id = item.itemId();
            productId = itemProductProductId( item );
            quantity = item.quantity();
            unitPriceAmount = itemProductUnitPriceAmount( item );
        }

        String unitPriceCurrency = item.product().unitPrice().currency().getCurrencyCode();

        CartItemReadModelEntity cartItemReadModelEntity = new CartItemReadModelEntity( id, cartId1, productId, quantity, unitPriceAmount, unitPriceCurrency );

        return cartItemReadModelEntity;
    }

    @Override
    public CartItemSnapshot toSnapshot(CartItem item) {
        if ( item == null ) {
            return null;
        }

        String productId = null;
        Money unitPrice = null;
        int quantity = 0;

        productId = itemProductProductId( item );
        unitPrice = itemProductUnitPrice( item );
        quantity = item.quantity();

        CartItemSnapshot cartItemSnapshot = new CartItemSnapshot( productId, quantity, unitPrice );

        return cartItemSnapshot;
    }

    private String itemProductProductId(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ProductRef product = cartItem.product();
        if ( product == null ) {
            return null;
        }
        String productId = product.productId();
        if ( productId == null ) {
            return null;
        }
        return productId;
    }

    private BigDecimal itemProductUnitPriceAmount(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ProductRef product = cartItem.product();
        if ( product == null ) {
            return null;
        }
        Money unitPrice = product.unitPrice();
        if ( unitPrice == null ) {
            return null;
        }
        BigDecimal amount = unitPrice.amount();
        if ( amount == null ) {
            return null;
        }
        return amount;
    }

    private Money itemProductUnitPrice(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ProductRef product = cartItem.product();
        if ( product == null ) {
            return null;
        }
        Money unitPrice = product.unitPrice();
        if ( unitPrice == null ) {
            return null;
        }
        return unitPrice;
    }
}
