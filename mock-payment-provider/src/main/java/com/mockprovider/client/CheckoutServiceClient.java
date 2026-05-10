package com.mockprovider.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "checkout-service", url = "${checkout.service.url:http://localhost:8080}")
public interface CheckoutServiceClient {

    @PostMapping("/payments/webhook")
    ResponseEntity<Void> sendWebhook(@RequestBody Map<String, String> body);
}
