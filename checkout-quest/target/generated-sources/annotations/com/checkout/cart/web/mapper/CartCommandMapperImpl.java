package com.checkout.cart.web.mapper;

import com.checkout.cart.application.usecase.additem.AddItemCommand;
import com.checkout.cart.web.dto.AddItemRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class CartCommandMapperImpl implements CartCommandMapper {

    @Override
    public AddItemCommand toCommand(AddItemRequest request, String cartId) {
        if ( request == null && cartId == null ) {
            return null;
        }

        AddItemCommand.AddItemCommandBuilder addItemCommand = AddItemCommand.builder();

        if ( request != null ) {
            addItemCommand.productId( request.productId() );
            addItemCommand.quantity( request.quantity() );
            addItemCommand.unitPrice( request.unitPrice() );
            addItemCommand.currency( request.currency() );
        }
        addItemCommand.cartId( cartId );

        return addItemCommand.build();
    }
}
