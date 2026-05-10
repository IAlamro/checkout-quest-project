package com.checkout.payment.domain.port;

import com.checkout.payment.domain.ProviderRef;
import com.checkout.shared.domain.Money;

public interface PaymentGateway {

    ProviderRef createIntent(String paymentId, Money amount);
}
