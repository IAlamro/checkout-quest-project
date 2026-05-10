package com.checkout.ledger.infrastructure.projection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalEntryJpaRepository extends JpaRepository<JournalEntryReadModelEntity, String> {

    boolean existsByReferenceIdAndReferenceType(String referenceId, String referenceType);
}
