package com.checkout.order.application.mapper;

import com.checkout.cart.application.api.CartItemSnapshot;
import com.checkout.order.domain.OrderItem;
import com.checkout.shared.domain.Money;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class OrderApplicationMapperImpl implements OrderApplicationMapper {

    @Override
    public OrderItem toOrderItem(CartItemSnapshot snapshot) {
        if ( snapshot == null ) {
            return null;
        }

        String productId = null;
        int quantity = 0;
        Money unitPrice = null;

        productId = snapshot.productId();
        quantity = snapshot.quantity();
        unitPrice = snapshot.unitPrice();

        OrderItem orderItem = new OrderItem( productId, quantity, unitPrice );

        return orderItem;
    }
}
