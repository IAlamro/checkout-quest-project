package com.checkout.shared.infrastructure.eventsourcing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, String> {

    List<EventEntity> findByStreamIdAndStreamTypeOrderByVersionAsc(String streamId, String streamType);

    @Query("SELECT MAX(e.version) FROM EventEntity e WHERE e.streamId = :streamId AND e.streamType = :streamType")
    Optional<Long> findMaxVersion(@Param("streamId") String streamId, @Param("streamType") String streamType);
}
