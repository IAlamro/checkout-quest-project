package com.checkout.payment.infrastructure.gateway;

import com.checkout.payment.domain.ProviderRef;
import com.checkout.payment.domain.port.PaymentGateway;
import com.checkout.payment.infrastructure.gateway.dto.CreateIntentRequest;
import com.checkout.payment.infrastructure.gateway.dto.CreateIntentResponse;
import com.checkout.shared.domain.Money;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import static com.checkout.shared.infrastructure.config.CircuitBreakerConfig.PAYMENT_PROVIDER_CB;

@Component
@RequiredArgsConstructor
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);

    private final MockProviderClient mockProviderClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Value("${mock.provider.url:}")
    private String mockProviderUrl;

    @Override
    public ProviderRef createIntent(String paymentId, Money amount) {
        if (mockProviderUrl == null || mockProviderUrl.isBlank()) {
            String localRef = "mock-" + UuidV7.generateAsString();
            log.debug("mock.provider.url not set - using local providerRef {}", localRef);
            return new ProviderRef(localRef);
        }

        var request = new CreateIntentRequest(
                paymentId,
                amount.amount().toPlainString(),
                amount.currency().getCurrencyCode()
        );

        return circuitBreakerFactory.create(PAYMENT_PROVIDER_CB).run(
                () -> {
                    CreateIntentResponse response = mockProviderClient.createIntent(request);
                    if (response == null || response.providerRef() == null) {
                        throw new IllegalStateException(
                                "Mock provider returned no providerRef for payment " + paymentId);
                    }
                    return new ProviderRef(response.providerRef());
                },
                throwable -> {
                    log.warn("Payment provider call failed for payment {} — circuit breaker state: {}",
                            paymentId, throwable.getMessage());
                    throw new PaymentProviderUnavailableException(paymentId, throwable);
                }
        );
    }
}
