package com.checkout.payment.infrastructure;

import com.checkout.payment.domain.Payment;
import com.checkout.shared.eventsourcing.AggregateRepository;
import com.checkout.shared.eventsourcing.EventStore;
import com.checkout.shared.util.JsonMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentAggregateRepository extends AggregateRepository<Payment> {

    public PaymentAggregateRepository(EventStore eventStore, JsonMapper jsonMapper,
                                       ApplicationEventPublisher eventPublisher) {
        super(eventStore, jsonMapper, eventPublisher);
    }

    @Override
    protected Payment createEmpty() { return Payment.reconstitute(); }

    @Override
    protected String streamType() { return "payment"; }

    @Override
    protected String streamId(Payment aggregate) { return aggregate.getId().value(); }
}
