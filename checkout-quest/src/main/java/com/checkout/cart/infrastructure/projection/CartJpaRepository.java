package com.checkout.cart.infrastructure.projection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartJpaRepository extends JpaRepository<CartReadModelEntity, String> {}
