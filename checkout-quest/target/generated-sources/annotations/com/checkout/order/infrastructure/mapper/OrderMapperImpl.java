package com.checkout.order.infrastructure.mapper;

import com.checkout.order.domain.OrderItem;
import com.checkout.order.infrastructure.projection.OrderItemReadModelEntity;
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
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderItemReadModelEntity toReadModel(String itemId, String orderId, OrderItem item) {
        if ( itemId == null && orderId == null && item == null ) {
            return null;
        }

        String productId = null;
        int quantity = 0;
        BigDecimal unitPriceAmount = null;
        if ( item != null ) {
            productId = item.productId();
            quantity = item.quantity();
            unitPriceAmount = itemUnitPriceAmount( item );
        }
        String id = null;
        id = itemId;
        String orderId1 = null;
        orderId1 = orderId;

        String unitPriceCurrency = item.unitPrice().currency().getCurrencyCode();

        OrderItemReadModelEntity orderItemReadModelEntity = new OrderItemReadModelEntity( id, orderId1, productId, quantity, unitPriceAmount, unitPriceCurrency );

        return orderItemReadModelEntity;
    }

    private BigDecimal itemUnitPriceAmount(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Money unitPrice = orderItem.unitPrice();
        if ( unitPrice == null ) {
            return null;
        }
        BigDecimal amount = unitPrice.amount();
        if ( amount == null ) {
            return null;
        }
        return amount;
    }
}
