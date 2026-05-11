package com.checkout.shared.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    public static final String PAYMENT_PROVIDER_CB = "payment-provider";

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> paymentProviderCircuitBreaker() {
        return factory -> factory.configure(builder -> builder
                        .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                                // Evaluate over the last 5 calls
                                .slidingWindowType(SlidingWindowType.COUNT_BASED)
                                .slidingWindowSize(5)
                                // Do not open before at least 3 calls are recorded
                                .minimumNumberOfCalls(3)
                                // Open the circuit when 50% or more calls fail
                                .failureRateThreshold(50)
                                // Stay open for 10 s, then move to half-open
                                .waitDurationInOpenState(Duration.ofSeconds(10))
                                // Allow 2 probe calls in half-open before deciding to close or re-open
                                .permittedNumberOfCallsInHalfOpenState(2)
                                .build())
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                // Give the provider 5 seconds to respond
                                .timeoutDuration(Duration.ofSeconds(5))
                                .build()),
                PAYMENT_PROVIDER_CB);
    }
}
