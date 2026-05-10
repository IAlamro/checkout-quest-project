package com.checkout.cart.application.api;

/**
 * Cross-module API for the Cart context.
 * Only order.application may import this interface.
 */
public interface CartFacade {

    CartSnapshot getSnapshot(String cartId);

    void lock(String cartId);
}
