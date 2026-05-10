package com.checkout.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Checkout Quest API")
                        .version("1.0.0")
                        .description("""
                                REST API for the Checkout Quest platform - a modular monolith covering the full \
                                purchase lifecycle: cart management, order creation, payment processing through \
                                an external provider, and double-entry bookkeeping.

                                **Happy-path call order**
                                1. `POST /carts`
                                2. `POST /carts/{cartId}/items`
                                3. `POST /carts/{cartId}/checkout`
                                4. `POST /orders/{orderId}/payment/start`
                                5. Confirm via mock provider - `POST http://localhost:8081/payments/{providerRef}/confirm`
                                6. `GET /orders/{orderId}/payments` - expect CONFIRMED
                                7. `GET /ledger/trial-balance` - expect balanced: true
                                """)
                        .contact(new Contact()
                                .name("Ibrahim Alamro")
                                .email("ibrahimalamro760@gmail.com")));
    }
}
