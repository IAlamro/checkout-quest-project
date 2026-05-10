package com.mockprovider.web;

import com.mockprovider.client.CheckoutServiceClient;
import com.mockprovider.web.dto.RegisterPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class MockProviderController {

    private static final Logger log = LoggerFactory.getLogger(MockProviderController.class);

    private final ConcurrentHashMap<String, String> registry = new ConcurrentHashMap<>();

    private final CheckoutServiceClient checkoutServiceClient;

    /**
     * Called by the checkout service when a payment is initiated.
     * Returns a providerRef that the checkout service stores and later uses to trigger webhooks.
     */
    @PostMapping("/payments")
    public ResponseEntity<Map<String, String>> registerPayment(@RequestBody RegisterPaymentRequest request) {
        String providerRef = "mock-" + UUID.randomUUID().toString().replace("-", "");
        registry.put(providerRef, request.paymentId());
        log.info("Registered payment intent: paymentId={} providerRef={} amount={} {}",
                request.paymentId(), providerRef, request.amount(), request.currency());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("providerRef", providerRef, "status", "PENDING"));
    }

    /**
     * Admin endpoint — simulates the provider confirming a payment.
     * Sends AUTHORIZED then CONFIRMED webhooks to the checkout service.
     */
    @PostMapping("/payments/{providerRef}/confirm")
    public ResponseEntity<Map<String, String>> confirm(@PathVariable String providerRef) {
        if (!registry.containsKey(providerRef)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Unknown providerRef: " + providerRef));
        }
        log.info("Confirming payment for providerRef={}", providerRef);
        sendWebhook(providerRef, "AUTHORIZED", null);
        sendWebhook(providerRef, "CONFIRMED", null);
        registry.remove(providerRef);
        return ResponseEntity.ok(Map.of("providerRef", providerRef, "status", "CONFIRMED"));
    }

    /**
     * Admin endpoint — simulates the provider declining a payment.
     * Sends a FAILED webhook to the checkout service.
     */
    @PostMapping("/payments/{providerRef}/fail")
    public ResponseEntity<Map<String, String>> fail(
            @PathVariable String providerRef,
            @RequestParam(defaultValue = "Provider declined") String reason) {
        if (!registry.containsKey(providerRef)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Unknown providerRef: " + providerRef));
        }
        log.info("Failing payment for providerRef={} reason={}", providerRef, reason);
        sendWebhook(providerRef, "FAILED", reason);
        registry.remove(providerRef);
        return ResponseEntity.ok(Map.of("providerRef", providerRef, "status", "FAILED", "reason", reason));
    }

    /**
     * Lists all pending (not yet resolved) payment intents — useful for debugging.
     */
    @GetMapping("/payments")
    public ResponseEntity<Map<String, String>> listPending() {
        return ResponseEntity.ok(Map.copyOf(registry));
    }

    private void sendWebhook(String providerRef, String type, String reason) {
        var body = new java.util.HashMap<String, String>();
        body.put("eventId", UUID.randomUUID().toString());
        body.put("providerRef", providerRef);
        body.put("type", type);
        if (reason != null) body.put("reason", reason);

        try {
            checkoutServiceClient.sendWebhook(body);
            log.info("Sent {} webhook for providerRef={}", type, providerRef);
        } catch (Exception e) {
            log.error("Failed to send {} webhook for providerRef={}: {}", type, providerRef, e.getMessage());
            throw new RuntimeException("Webhook delivery to checkout service failed: " + e.getMessage(), e);
        }
    }
}
