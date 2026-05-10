package com.checkout.order.web;

import com.checkout.order.application.usecase.checkoutcart.CheckoutCartCommand;
import com.checkout.order.application.usecase.checkoutcart.CheckoutCartHandler;
import com.checkout.order.web.dto.CheckoutResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order creation - converts a locked cart into an order ready for payment")
public class OrderController {

    private final CheckoutCartHandler checkoutCartHandler;

    @PostMapping("/{cartId}/checkout")
    @Operation(
            summary = "Checkout a cart",
            description = "Locks the cart and creates an order from its contents. The cart cannot receive new items after this call. Returns the new order identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cart is already locked or empty", content = @Content)
    })
    public ResponseEntity<CheckoutResponse> checkout(
            @Parameter(description = "Cart identifier to checkout", required = true) @PathVariable String cartId) {
        String orderId = checkoutCartHandler.handle(new CheckoutCartCommand(cartId));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CheckoutResponse(orderId));
    }
}
