package com.checkout.ledger.infrastructure.projection;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "ledger_entry_line")
public class EntryLineReadModelEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "entry_id", nullable = false)
    private String entryId;

    @Column(name = "account_code", nullable = false)
    private String accountCode;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "side", nullable = false)
    private String side;

    @Column(name = "description")
    private String description;
}
