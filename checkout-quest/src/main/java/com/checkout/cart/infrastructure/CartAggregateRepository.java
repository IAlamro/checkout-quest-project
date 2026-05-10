package com.checkout.cart.infrastructure;

import com.checkout.cart.domain.Cart;
import com.checkout.shared.eventsourcing.AggregateRepository;
import com.checkout.shared.eventsourcing.EventStore;
import com.checkout.shared.util.JsonMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
public class CartAggregateRepository extends AggregateRepository<Cart> {

    public CartAggregateRepository(EventStore eventStore, JsonMapper jsonMapper,
                                    ApplicationEventPublisher eventPublisher) {
        super(eventStore, jsonMapper, eventPublisher);
    }

    @Override
    protected Cart createEmpty() { return Cart.reconstitute(); }

    @Override
    protected String streamType() { return "cart"; }

    @Override
    protected String streamId(Cart aggregate) { return aggregate.getId().value(); }
}
