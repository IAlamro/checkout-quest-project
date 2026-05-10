package com.checkout.order.web;

import com.checkout.order.application.usecase.getorder.GetOrderHandler;
import com.checkout.order.application.usecase.getorder.GetOrderQuery;
import com.checkout.order.web.dto.OrderResponse;
import com.checkout.order.web.mapper.OrderWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order creation - converts a locked cart into an order ready for payment")
public class OrderResourceController {

    private final GetOrderHandler getOrderHandler;
    private final OrderWebMapper orderWebMapper;

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Get an order",
            description = "Returns the current order state. The `status` field progresses from CREATED through PENDING_PAYMENT to PAID or PAYMENT_FAILED as payment events arrive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order identifier", required = true) @PathVariable String orderId) {
        return ResponseEntity.ok(orderWebMapper.toResponse(getOrderHandler.handle(new GetOrderQuery(orderId))));
    }
}
