package com.checkout.ledger.application.usecase.getjournalentries;

import com.checkout.ledger.infrastructure.projection.JournalEntryJpaRepository;
import com.checkout.ledger.infrastructure.projection.JournalEntryReadModelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetJournalEntriesHandler {

    private final JournalEntryJpaRepository journalEntryJpaRepository;

    @Transactional(readOnly = true)
    public List<JournalEntryReadModelEntity> handle() {
        return journalEntryJpaRepository.findAll();
    }
}
