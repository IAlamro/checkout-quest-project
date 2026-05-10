package com.checkout.ledger.infrastructure;

import com.checkout.ledger.domain.JournalEntry;
import com.checkout.shared.eventsourcing.AggregateRepository;
import com.checkout.shared.eventsourcing.EventStore;
import com.checkout.shared.util.JsonMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
public class JournalEntryAggregateRepository extends AggregateRepository<JournalEntry> {

    public JournalEntryAggregateRepository(EventStore eventStore, JsonMapper jsonMapper,
                                            ApplicationEventPublisher eventPublisher) {
        super(eventStore, jsonMapper, eventPublisher);
    }

    @Override
    protected JournalEntry createEmpty() { return JournalEntry.reconstitute(); }

    @Override
    protected String streamType() { return "ledger-journal"; }

    @Override
    protected String streamId(JournalEntry aggregate) { return aggregate.getId().value(); }
}
