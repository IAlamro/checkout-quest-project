package com.checkout.shared.infrastructure.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyJpaRepository extends JpaRepository<IdempotencyEntity, String> {}
