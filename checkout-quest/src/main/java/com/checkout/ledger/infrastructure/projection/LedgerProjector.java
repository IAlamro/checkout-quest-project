package com.checkout.ledger.infrastructure.projection;

import com.checkout.ledger.domain.Side;
import com.checkout.ledger.domain.event.JournalEntryPosted;
import com.checkout.ledger.infrastructure.mapper.LedgerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LedgerProjector {

    private final JournalEntryJpaRepository journalEntryJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final LedgerMapper ledgerMapper;

    @EventListener
    @Transactional
    public void on(JournalEntryPosted event) {
        JournalEntryReadModelEntity entry = new JournalEntryReadModelEntity(
                event.entryId().value(), event.referenceId(),
                event.referenceType().name(), event.occurredAt()
        );

        event.lines().forEach(line -> {
            entry.addLine(ledgerMapper.toReadModel(event.entryId().value(), line));

            // Update account balance
            accountJpaRepository.findByCode(line.accountCode().value()).ifPresent(account -> {
                if (line.side() == Side.DEBIT) {
                    account.applyDebit(line.amount().amount());
                } else {
                    account.applyCredit(line.amount().amount());
                }
                accountJpaRepository.save(account);
            });
        });

        journalEntryJpaRepository.save(entry);
    }
}
