package com.checkout.order.web.mapper;

import com.checkout.order.infrastructure.projection.OrderItemReadModelEntity;
import com.checkout.order.infrastructure.projection.OrderReadModelEntity;
import com.checkout.order.web.dto.OrderItemResponse;
import com.checkout.order.web.dto.OrderResponse;
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
public class OrderWebMapperImpl implements OrderWebMapper {

    @Override
    public OrderResponse toResponse(OrderReadModelEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String orderId = null;
        String cartId = null;
        String status = null;
        BigDecimal totalAmount = null;
        String currency = null;
        Instant createdAt = null;
        List<OrderItemResponse> items = null;

        orderId = entity.getOrderId();
        cartId = entity.getCartId();
        status = entity.getStatus();
        totalAmount = entity.getTotalAmount();
        currency = entity.getCurrency();
        createdAt = entity.getCreatedAt();
        items = orderItemReadModelEntityListToOrderItemResponseList( entity.getItems() );

        OrderResponse orderResponse = new OrderResponse( orderId, cartId, status, totalAmount, currency, createdAt, items );

        return orderResponse;
    }

    @Override
    public OrderItemResponse toResponse(OrderItemReadModelEntity entity) {
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

        OrderItemResponse orderItemResponse = new OrderItemResponse( productId, quantity, unitPriceAmount, unitPriceCurrency );

        return orderItemResponse;
    }

    protected List<OrderItemResponse> orderItemReadModelEntityListToOrderItemResponseList(List<OrderItemReadModelEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemResponse> list1 = new ArrayList<OrderItemResponse>( list.size() );
        for ( OrderItemReadModelEntity orderItemReadModelEntity : list ) {
            list1.add( toResponse( orderItemReadModelEntity ) );
        }

        return list1;
    }
}
