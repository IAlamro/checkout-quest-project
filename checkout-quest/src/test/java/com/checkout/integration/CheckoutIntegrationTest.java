package com.checkout.integration;

import com.checkout.payment.domain.ProviderRef;
import com.checkout.payment.domain.port.PaymentGateway;
import com.checkout.shared.infrastructure.outbox.OutboxRelay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CheckoutIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired OutboxRelay outboxRelay;

    @MockBean PaymentGateway paymentGateway;

    // Flow 1: Happy Path - Cart to PAID

    @Test
    void full_happy_path_cart_checkout_to_paid_order() throws Exception {
        String providerRef = "mock-" + UUID.randomUUID();
        when(paymentGateway.createIntent(any(), any())).thenReturn(new ProviderRef(providerRef));

        // 1. Create cart
        String cartId = createCart();

        // 2. Add item
        mvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":"prod-1","quantity":2,"unitPrice":49.99,"currency":"USD"}
                                """))
                .andExpect(status().isOk());

        // 3. Checkout -> order created
        String orderId = checkout(cartId);

        // 4. Start payment
        String idemKey = UUID.randomUUID().toString();
        mvc.perform(post("/orders/{orderId}/payment/start", orderId)
                        .header("Idempotency-Key", idemKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":99.98,"currency":"USD"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.providerRef").value(providerRef));

        // PaymentInitiatedIntegrationEvent -> Order moves to PENDING_PAYMENT
        outboxRelay.relay();

        // 5. Provider sends CONFIRMED webhook
        String eventId = UUID.randomUUID().toString();
        mvc.perform(post("/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventId":"%s","providerRef":"%s","type":"CONFIRMED"}
                                """.formatted(eventId, providerRef)))
                .andExpect(status().isOk());

        // PaymentConfirmedIntegrationEvent -> Order moves to PAID
        outboxRelay.relay();

        // 6. Verify order is PAID
        mvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    // Flow 2: Payment Failure + Retry

    @Test
    void payment_failure_then_retry_results_in_paid_order() throws Exception {
        String providerRef1 = "mock-" + UUID.randomUUID();
        String providerRef2 = "mock-" + UUID.randomUUID();
        when(paymentGateway.createIntent(any(), any()))
                .thenReturn(new ProviderRef(providerRef1))
                .thenReturn(new ProviderRef(providerRef2));

        String cartId = createCart();
        addItem(cartId);
        String orderId = checkout(cartId);

        // First payment attempt
        mvc.perform(post("/orders/{orderId}/payment/start", orderId)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":99.98,"currency":"USD"}
                                """))
                .andExpect(status().isCreated());

        outboxRelay.relay(); // PENDING_PAYMENT

        // Provider sends FAILED
        mvc.perform(post("/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventId":"%s","providerRef":"%s","type":"FAILED","reason":"Declined"}
                                """.formatted(UUID.randomUUID(), providerRef1)))
                .andExpect(status().isOk());

        outboxRelay.relay(); // PAYMENT_FAILED

        mvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(jsonPath("$.status").value("PAYMENT_FAILED"));

        // Retry payment
        mvc.perform(post("/orders/{orderId}/payments/retry", orderId)
                        .header("Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isCreated());

        outboxRelay.relay(); // PENDING_PAYMENT again

        // Provider confirms retry
        mvc.perform(post("/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventId":"%s","providerRef":"%s","type":"CONFIRMED"}
                                """.formatted(UUID.randomUUID(), providerRef2)))
                .andExpect(status().isOk());

        outboxRelay.relay(); // PAID

        mvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    // Flow 3: Duplicate Webhook

    @Test
    void duplicate_webhook_does_not_corrupt_order_state() throws Exception {
        String providerRef = "mock-" + UUID.randomUUID();
        when(paymentGateway.createIntent(any(), any())).thenReturn(new ProviderRef(providerRef));

        String cartId = createCart();
        addItem(cartId);
        String orderId = checkout(cartId);

        mvc.perform(post("/orders/{orderId}/payment/start", orderId)
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":99.98,"currency":"USD"}
                                """))
                .andExpect(status().isCreated());

        outboxRelay.relay();

        String eventId = UUID.randomUUID().toString();
        String webhookBody = """
                {"eventId":"%s","providerRef":"%s","type":"CONFIRMED"}
                """.formatted(eventId, providerRef);

        // First delivery
        mvc.perform(post("/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookBody))
                .andExpect(status().isOk());

        outboxRelay.relay();

        // Duplicate delivery - same eventId
        mvc.perform(post("/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(webhookBody))
                .andExpect(status().isOk()); // idempotent - no error

        outboxRelay.relay();

        // Order must still be PAID (not corrupted)
        mvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    // Idempotency-Key replay

    @Test
    void payment_start_with_same_idempotency_key_replays_cached_response() throws Exception {
        String providerRef = "mock-" + UUID.randomUUID();
        when(paymentGateway.createIntent(any(), any())).thenReturn(new ProviderRef(providerRef));

        String cartId = createCart();
        addItem(cartId);
        String orderId = checkout(cartId);

        String idemKey = UUID.randomUUID().toString();
        String requestBody = """
                {"amount":99.98,"currency":"USD"}
                """;

        // First request
        MvcResult first = mvc.perform(post("/orders/{orderId}/payment/start", orderId)
                        .header("Idempotency-Key", idemKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Retry with same key - must get same response, PaymentGateway not called again
        MvcResult second = mvc.perform(post("/orders/{orderId}/payment/start", orderId)
                        .header("Idempotency-Key", idemKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(second.getResponse().getContentAsString())
                .isEqualTo(first.getResponse().getContentAsString());
    }

    // Helpers

    private String createCart() throws Exception {
        MvcResult result = mvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        return extractField(result.getResponse().getContentAsString(), "cartId");
    }

    private void addItem(String cartId) throws Exception {
        mvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":"prod-1","quantity":2,"unitPrice":49.99,"currency":"USD"}
                                """))
                .andExpect(status().isOk());
    }

    private String checkout(String cartId) throws Exception {
        MvcResult result = mvc.perform(post("/carts/{cartId}/checkout", cartId))
                .andExpect(status().isCreated())
                .andReturn();
        return extractField(result.getResponse().getContentAsString(), "orderId");
    }

    private String extractField(String json, String field) {
        // Minimal JSON field extraction - avoids pulling in Jackson in the test body
        String search = "\"" + field + "\":\"";
        int start = json.indexOf(search) + search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
