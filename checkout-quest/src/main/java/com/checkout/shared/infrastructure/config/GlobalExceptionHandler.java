package com.checkout.shared.infrastructure.config;

import com.checkout.cart.application.usecase.getcart.GetCartHandler;
import com.checkout.cart.domain.Cart;
import com.checkout.ledger.domain.DoubleEntryService;
import com.checkout.order.application.usecase.getorder.GetOrderHandler;
import com.checkout.order.domain.Order;
import com.checkout.order.domain.InvalidOrderTransitionException;
import com.checkout.payment.application.usecase.getpayment.GetPaymentHandler;
import com.checkout.payment.application.usecase.initiatepayment.InitiatePaymentHandler;
import com.checkout.payment.domain.Payment;
import com.checkout.payment.infrastructure.gateway.PaymentProviderUnavailableException;
import com.checkout.shared.eventsourcing.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EventStore.StreamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStreamNotFound(EventStore.StreamNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GetCartHandler.CartNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartNotFound(GetCartHandler.CartNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GetOrderHandler.OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(GetOrderHandler.OrderNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GetPaymentHandler.PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(GetPaymentHandler.PaymentNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Cart.CartLockedException.class)
    public ResponseEntity<ErrorResponse> handleCartLocked(Cart.CartLockedException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EventStore.ConcurrencyException.class)
    public ResponseEntity<ErrorResponse> handleConcurrency(EventStore.ConcurrencyException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InitiatePaymentHandler.ActivePaymentExistsException.class)
    public ResponseEntity<ErrorResponse> handleActivePaymentExists(InitiatePaymentHandler.ActivePaymentExistsException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Order.DoublePaymentException.class)
    public ResponseEntity<ErrorResponse> handleDoublePayment(Order.DoublePaymentException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderTransition(InvalidOrderTransitionException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(Payment.InvalidPaymentStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentState(Payment.InvalidPaymentStateException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(DoubleEntryService.UnbalancedEntryException.class)
    public ResponseEntity<ErrorResponse> handleUnbalancedEntry(DoubleEntryService.UnbalancedEntryException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(PaymentProviderUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleProviderUnavailable(PaymentProviderUnavailableException ex) {
        log.warn("Payment provider unavailable: {}", ex.getMessage());
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now().toString()
        ));
    }
}
