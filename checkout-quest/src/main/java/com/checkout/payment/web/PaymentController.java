package com.checkout.payment.web;

import com.checkout.payment.application.usecase.getpayment.GetPaymentHandler;
import com.checkout.payment.application.usecase.getpayment.GetPaymentQuery;
import com.checkout.payment.application.usecase.initiatepayment.InitiatePaymentHandler;
import com.checkout.payment.application.usecase.initiatepayment.PaymentResult;
import com.checkout.payment.application.usecase.retrypayment.RetryPaymentCommand;
import com.checkout.payment.application.usecase.retrypayment.RetryPaymentHandler;
import com.checkout.payment.web.dto.InitiatePaymentRequest;
import com.checkout.payment.web.dto.PaymentInitiatedResponse;
import com.checkout.payment.web.dto.PaymentResponse;
import com.checkout.payment.web.mapper.PaymentCommandMapper;
import com.checkout.payment.web.mapper.PaymentWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment lifecycle - initiate, retry, and query payments for an order")
public class PaymentController {

    private final InitiatePaymentHandler initiatePaymentHandler;
    private final RetryPaymentHandler retryPaymentHandler;
    private final GetPaymentHandler getPaymentHandler;
    private final PaymentCommandMapper paymentCommandMapper;
    private final PaymentWebMapper paymentWebMapper;

    @PostMapping("/{orderId}/payment/start")
    @Operation(
            summary = "Initiate a payment",
            description = "Creates a payment intent with the external provider and moves the order to PENDING_PAYMENT. " +
                    "The providerRef in the response is the provider's own reference - use it to confirm or fail the payment " +
                    "through the mock provider. Supports the Idempotency-Key header to prevent duplicate payments on retries."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment initiated, provider reference returned"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "An active payment already exists for this order", content = @Content)
    })
    public ResponseEntity<PaymentInitiatedResponse> initiatePayment(
            @Parameter(description = "Order identifier", required = true) @PathVariable String orderId,
            @Valid @RequestBody InitiatePaymentRequest request,
            @Parameter(description = "Optional idempotency key - replaying the same key returns the cached response without creating a second payment")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        PaymentResult result = initiatePaymentHandler.handle(
                paymentCommandMapper.toCommand(request, orderId, idempotencyKey));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PaymentInitiatedResponse(result.paymentId(), result.providerRef()));
    }

    @PostMapping("/{orderId}/payments/retry")
    @Operation(
            summary = "Retry a failed payment",
            description = "Creates a fresh payment attempt for an order whose previous payment failed. " +
                    "The amount and currency are carried over from the failed payment automatically - no request body needed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retry payment initiated"),
            @ApiResponse(responseCode = "404", description = "Order not found or no failed payment to retry", content = @Content),
            @ApiResponse(responseCode = "409", description = "An active payment already exists for this order", content = @Content)
    })
    public ResponseEntity<PaymentInitiatedResponse> retryPayment(
            @Parameter(description = "Order identifier", required = true) @PathVariable String orderId,
            @Parameter(description = "Optional idempotency key")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        PaymentResult result = retryPaymentHandler.handle(new RetryPaymentCommand(orderId, idempotencyKey));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PaymentInitiatedResponse(result.paymentId(), result.providerRef()));
    }

    @GetMapping("/{orderId}/payments")
    @Operation(
            summary = "Get the current payment",
            description = "Returns the latest payment record for the order. Status moves from INITIATED through AUTHORIZED to CONFIRMED or FAILED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "No payment found for this order", content = @Content)
    })
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "Order identifier", required = true) @PathVariable String orderId) {
        return ResponseEntity.ok(paymentWebMapper.toResponse(getPaymentHandler.handle(new GetPaymentQuery(orderId))));
    }
}
