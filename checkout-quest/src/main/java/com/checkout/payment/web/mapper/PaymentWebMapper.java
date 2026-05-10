package com.checkout.payment.web.mapper;

import com.checkout.payment.infrastructure.projection.PaymentReadModelEntity;
import com.checkout.payment.web.dto.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentWebMapper {

    PaymentResponse toResponse(PaymentReadModelEntity entity);
}
