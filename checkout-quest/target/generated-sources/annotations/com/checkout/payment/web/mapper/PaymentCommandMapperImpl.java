package com.checkout.payment.web.mapper;

import com.checkout.payment.application.usecase.handlewebhook.HandleWebhookCommand;
import com.checkout.payment.application.usecase.initiatepayment.InitiatePaymentCommand;
import com.checkout.payment.web.dto.InitiatePaymentRequest;
import com.checkout.payment.web.dto.WebhookRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class PaymentCommandMapperImpl implements PaymentCommandMapper {

    @Override
    public InitiatePaymentCommand toCommand(InitiatePaymentRequest request, String orderId, String idempotencyKey) {
        if ( request == null && orderId == null && idempotencyKey == null ) {
            return null;
        }

        InitiatePaymentCommand.InitiatePaymentCommandBuilder initiatePaymentCommand = InitiatePaymentCommand.builder();

        if ( request != null ) {
            initiatePaymentCommand.amount( request.amount() );
            initiatePaymentCommand.currency( request.currency() );
        }
        initiatePaymentCommand.orderId( orderId );
        initiatePaymentCommand.idempotencyKey( idempotencyKey );

        return initiatePaymentCommand.build();
    }

    @Override
    public HandleWebhookCommand toCommand(WebhookRequest request) {
        if ( request == null ) {
            return null;
        }

        HandleWebhookCommand.HandleWebhookCommandBuilder handleWebhookCommand = HandleWebhookCommand.builder();

        handleWebhookCommand.type( parseWebhookType( request.type() ) );
        handleWebhookCommand.eventId( request.eventId() );
        handleWebhookCommand.providerRef( request.providerRef() );
        handleWebhookCommand.reason( request.reason() );

        return handleWebhookCommand.build();
    }
}
