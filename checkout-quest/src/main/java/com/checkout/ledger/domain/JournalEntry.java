package com.checkout.ledger.domain;

import com.checkout.ledger.domain.event.JournalEntryPosted;
import com.checkout.shared.domain.AggregateRoot;
import com.checkout.shared.domain.DomainEvent;
import com.checkout.shared.domain.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JournalEntry extends AggregateRoot {

    private JournalEntryId id;
    private String referenceId;
    private ReferenceType referenceType;
    private List<EntryLine> lines = new ArrayList<>();

    private JournalEntry() {}

    public static JournalEntry reconstitute() { return new JournalEntry(); }

    public static JournalEntry post(JournalEntryId entryId, String referenceId,
                                     ReferenceType referenceType, List<EntryLine> lines) {
        DoubleEntryService.validate(lines);
        JournalEntry entry = new JournalEntry();
        entry.record(new JournalEntryPosted(entryId, referenceId, referenceType, lines, Instant.now()));
        return entry;
    }

    @Override
    protected void applyEvent(DomainEvent event) {
        if (event instanceof JournalEntryPosted e) {
            this.id = e.entryId();
            this.referenceId = e.referenceId();
            this.referenceType = e.referenceType();
            this.lines = new ArrayList<>(e.lines());
        } else {
            throw new IllegalArgumentException("Unknown JournalEntry event: " + event.getClass());
        }
    }

    public JournalEntryId getId() { return id; }
    public String getReferenceId() { return referenceId; }
    public ReferenceType getReferenceType() { return referenceType; }
    public List<EntryLine> getLines() { return Collections.unmodifiableList(lines); }

    /** Builds entry lines from a posting template and a single amount. */
    public static List<EntryLine> buildLines(PostingTemplate template, Money amount) {
        return template.lines().stream()
                .map(tl -> new EntryLine(tl.accountCode(), amount, tl.side(), tl.description()))
                .toList();
    }
}
