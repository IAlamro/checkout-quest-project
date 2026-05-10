package com.checkout.ledger.infrastructure.projection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountReadModelEntity, String> {

    Optional<AccountReadModelEntity> findByCode(String code);
}
