package com.checkout.ledger.infrastructure.projection;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ledger_journal_entry")
public class JournalEntryReadModelEntity {

    @Id
    @Column(name = "entry_id")
    private String entryId;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Column(name = "reference_type", nullable = false)
    private String referenceType;

    @Column(name = "posted_at", nullable = false)
    private Instant postedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entry_id")
    private List<EntryLineReadModelEntity> lines = new ArrayList<>();

    public JournalEntryReadModelEntity(String entryId, String referenceId, String referenceType, Instant postedAt) {
        this.entryId = entryId;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.postedAt = postedAt;
    }

    public void addLine(EntryLineReadModelEntity line) { lines.add(line); }
}
