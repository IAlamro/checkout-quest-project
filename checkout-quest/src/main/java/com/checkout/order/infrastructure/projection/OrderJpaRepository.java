package com.checkout.order.infrastructure.projection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderReadModelEntity, String> {}
