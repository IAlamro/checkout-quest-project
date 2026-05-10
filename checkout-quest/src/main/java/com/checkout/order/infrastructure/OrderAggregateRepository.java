package com.checkout.order.infrastructure;

import com.checkout.order.domain.Order;
import com.checkout.shared.eventsourcing.AggregateRepository;
import com.checkout.shared.eventsourcing.EventStore;
import com.checkout.shared.util.JsonMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
public class OrderAggregateRepository extends AggregateRepository<Order> {

    public OrderAggregateRepository(EventStore eventStore, JsonMapper jsonMapper,
                                     ApplicationEventPublisher eventPublisher) {
        super(eventStore, jsonMapper, eventPublisher);
    }

    @Override
    protected Order createEmpty() { return Order.reconstitute(); }

    @Override
    protected String streamType() { return "order"; }

    @Override
    protected String streamId(Order aggregate) { return aggregate.getId().value(); }
}
