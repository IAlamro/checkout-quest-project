package com.checkout.payment.infrastructure.projection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentReadModelEntity, String> {

    Optional<PaymentReadModelEntity> findByProviderRef(String providerRef);

    @Query("SELECT p FROM PaymentReadModelEntity p WHERE p.orderId = :orderId ORDER BY p.createdAt DESC LIMIT 1")
    Optional<PaymentReadModelEntity> findByOrderId(@Param("orderId") String orderId);
}
