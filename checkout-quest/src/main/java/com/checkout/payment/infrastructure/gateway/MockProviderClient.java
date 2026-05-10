package com.checkout.payment.infrastructure.gateway;

import com.checkout.payment.infrastructure.gateway.dto.CreateIntentRequest;
import com.checkout.payment.infrastructure.gateway.dto.CreateIntentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mock-payment-provider", url = "${mock.provider.url:http://localhost:8081}")
public interface MockProviderClient {

    @PostMapping("/payments")
    CreateIntentResponse createIntent(@RequestBody CreateIntentRequest request);
}
