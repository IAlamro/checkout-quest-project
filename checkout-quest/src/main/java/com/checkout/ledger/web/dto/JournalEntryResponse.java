package com.checkout.ledger.web.dto;

import java.time.Instant;
import java.util.List;

public record JournalEntryResponse(
        String entryId,
        String referenceId,
        String referenceType,
        Instant postedAt,
        List<EntryLineResponse> lines
) {}
