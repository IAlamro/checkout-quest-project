package com.checkout.ledger.application.usecase.postpaymentconfirmed;

import com.checkout.ledger.domain.*;
import com.checkout.ledger.infrastructure.JournalEntryAggregateRepository;
import com.checkout.ledger.infrastructure.projection.AccountJpaRepository;
import com.checkout.ledger.infrastructure.projection.JournalEntryJpaRepository;
import com.checkout.shared.cqrs.CommandHandler;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPaymentConfirmedHandler implements CommandHandler<PostPaymentConfirmedCommand, Void> {

    private final JournalEntryAggregateRepository journalEntryRepository;
    private final JournalEntryJpaRepository journalEntryJpaRepository;

    @Override
    @Transactional
    public Void handle(PostPaymentConfirmedCommand command) {
        // Idempotency: skip if already posted for this reference
        if (journalEntryJpaRepository.existsByReferenceIdAndReferenceType(
                command.paymentId(), ReferenceType.PAYMENT_CONFIRMED.name())) {
            return null;
        }

        PostingTemplate template = PostingRuleEngine.templateFor(ReferenceType.PAYMENT_CONFIRMED);
        List<EntryLine> lines = JournalEntry.buildLines(template, command.amount());

        JournalEntry entry = JournalEntry.post(
                new JournalEntryId(UuidV7.generateAsString()),
                command.paymentId(), ReferenceType.PAYMENT_CONFIRMED, lines
        );
        journalEntryRepository.save(entry);
        return null;
    }
}
