package com.checkout.payment.web.mapper;

import com.checkout.payment.application.usecase.handlewebhook.HandleWebhookCommand;
import com.checkout.payment.application.usecase.initiatepayment.InitiatePaymentCommand;
import com.checkout.payment.domain.WebhookType;
import com.checkout.payment.web.dto.InitiatePaymentRequest;
import com.checkout.payment.web.dto.WebhookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentCommandMapper {

    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "request.amount", target = "amount")
    @Mapping(source = "request.currency", target = "currency")
    @Mapping(source = "idempotencyKey", target = "idempotencyKey")
    InitiatePaymentCommand toCommand(InitiatePaymentRequest request, String orderId, String idempotencyKey);

    @Mapping(source = "type", target = "type", qualifiedByName = "parseWebhookType")
    HandleWebhookCommand toCommand(WebhookRequest request);

    @Named("parseWebhookType")
    default WebhookType parseWebhookType(String type) {
        return WebhookType.valueOf(type.toUpperCase());
    }
}
