package com.checkout.ledger.application.usecase.postpaymentfailed;

import com.checkout.ledger.domain.*;
import com.checkout.ledger.infrastructure.JournalEntryAggregateRepository;
import com.checkout.ledger.infrastructure.projection.JournalEntryJpaRepository;
import com.checkout.shared.cqrs.CommandHandler;
import com.checkout.shared.util.UuidV7;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPaymentFailedHandler implements CommandHandler<PostPaymentFailedCommand, Void> {

    private final JournalEntryAggregateRepository journalEntryRepository;
    private final JournalEntryJpaRepository journalEntryJpaRepository;

    @Override
    @Transactional
    public Void handle(PostPaymentFailedCommand command) {
        if (journalEntryJpaRepository.existsByReferenceIdAndReferenceType(
                command.paymentId(), ReferenceType.PAYMENT_FAILED.name())) {
            return null;
        }

        PostingTemplate template = PostingRuleEngine.templateFor(ReferenceType.PAYMENT_FAILED);
        List<EntryLine> lines = JournalEntry.buildLines(template, command.amount());

        JournalEntry entry = JournalEntry.post(
                new JournalEntryId(UuidV7.generateAsString()),
                command.paymentId(), ReferenceType.PAYMENT_FAILED, lines
        );
        journalEntryRepository.save(entry);
        return null;
    }
}
