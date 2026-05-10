package com.checkout.shared.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, String> {

    @Query("SELECT o FROM OutboxEntity o WHERE o.dispatchedAt IS NULL AND o.dead = FALSE ORDER BY o.createdAt ASC LIMIT :limit")
    List<OutboxEntity> findUndispatched(@Param("limit") int limit);
}
