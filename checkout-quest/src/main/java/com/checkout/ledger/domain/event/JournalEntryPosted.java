package com.checkout.ledger.domain.event;

import com.checkout.ledger.domain.EntryLine;
import com.checkout.ledger.domain.JournalEntryId;
import com.checkout.ledger.domain.ReferenceType;
import com.checkout.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.List;

public record JournalEntryPosted(
        JournalEntryId entryId,
        String referenceId,
        ReferenceType referenceType,
        List<EntryLine> lines,
        Instant occurredAt
) implements DomainEvent {}
