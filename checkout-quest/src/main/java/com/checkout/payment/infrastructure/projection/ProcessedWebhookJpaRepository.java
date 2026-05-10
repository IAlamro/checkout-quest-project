package com.checkout.payment.infrastructure.projection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedWebhookJpaRepository extends JpaRepository<ProcessedWebhookEventEntity, String> {}
