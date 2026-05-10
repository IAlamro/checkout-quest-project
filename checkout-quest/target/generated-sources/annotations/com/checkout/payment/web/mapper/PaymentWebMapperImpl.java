package com.checkout.payment.web.mapper;

import com.checkout.payment.infrastructure.projection.PaymentReadModelEntity;
import com.checkout.payment.web.dto.PaymentResponse;
import java.math.BigDecimal;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-10T21:45:10+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class PaymentWebMapperImpl implements PaymentWebMapper {

    @Override
    public PaymentResponse toResponse(PaymentReadModelEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String paymentId = null;
        String orderId = null;
        String status = null;
        BigDecimal amount = null;
        String currency = null;
        String providerRef = null;
        String idempotencyKey = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        paymentId = entity.getPaymentId();
        orderId = entity.getOrderId();
        status = entity.getStatus();
        amount = entity.getAmount();
        currency = entity.getCurrency();
        providerRef = entity.getProviderRef();
        idempotencyKey = entity.getIdempotencyKey();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();

        PaymentResponse paymentResponse = new PaymentResponse( paymentId, orderId, status, amount, currency, providerRef, idempotencyKey, createdAt, updatedAt );

        return paymentResponse;
    }
}
