package com.checkout.cart.web;

import com.checkout.cart.application.usecase.additem.AddItemHandler;
import com.checkout.cart.application.usecase.createcart.CreateCartCommand;
import com.checkout.cart.application.usecase.createcart.CreateCartHandler;
import com.checkout.cart.application.usecase.getcart.GetCartHandler;
import com.checkout.cart.application.usecase.getcart.GetCartQuery;
import com.checkout.cart.web.dto.AddItemRequest;
import com.checkout.cart.web.dto.CartResponse;
import com.checkout.cart.web.dto.CreateCartResponse;
import com.checkout.cart.web.mapper.CartCommandMapper;
import com.checkout.cart.web.mapper.CartWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management - create carts and add items before checking out")
public class CartController {

    private final CreateCartHandler createCartHandler;
    private final AddItemHandler addItemHandler;
    private final GetCartHandler getCartHandler;
    private final CartCommandMapper cartCommandMapper;
    private final CartWebMapper cartWebMapper;

    @PostMapping
    @Operation(summary = "Create a cart", description = "Opens a new empty shopping cart and returns its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cart created"),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public ResponseEntity<CreateCartResponse> createCart() {
        String cartId = createCartHandler.handle(new CreateCartCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCartResponse(cartId));
    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add an item to a cart", description = "Adds a product to an open cart. Returns the full cart state after the item is added. Fails with 409 if the cart is already locked.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added, cart returned"),
            @ApiResponse(responseCode = "400", description = "Validation error - check request body fields", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cart is locked and cannot accept new items", content = @Content)
    })
    public ResponseEntity<CartResponse> addItem(
            @Parameter(description = "Cart identifier", required = true) @PathVariable String cartId,
            @Valid @RequestBody AddItemRequest request) {
        addItemHandler.handle(cartCommandMapper.toCommand(request, cartId));
        return ResponseEntity.ok(cartWebMapper.toResponse(getCartHandler.handle(new GetCartQuery(cartId))));
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Get a cart", description = "Returns the current cart state including all items and the running total.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart found"),
            @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content)
    })
    public ResponseEntity<CartResponse> getCart(
            @Parameter(description = "Cart identifier", required = true) @PathVariable String cartId) {
        return ResponseEntity.ok(cartWebMapper.toResponse(getCartHandler.handle(new GetCartQuery(cartId))));
    }
}
