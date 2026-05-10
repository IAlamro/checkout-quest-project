# checkout-quest

A modular-monolith checkout service built with Spring Boot 3.3, event sourcing, and a transactional outbox. Accompanies a second, standalone service (`mock-payment-provider`) that simulates a payment gateway and fires webhooks back to this service.

## Architecture at a glance

```
checkout-quest (port 8080)
  cart       -- Cart bounded context (create, add items, lock)
  order      -- Order bounded context (checkout, state machine)
  payment    -- Payment bounded context (initiate, webhook processing)
  ledger     -- Double-entry ledger (journal entries on payment events)
  shared     -- Event store, outbox relay, idempotency filter

mock-payment-provider (port 8081)
  POST /payments             -- register a payment intent
  POST /payments/{ref}/confirm -- fire AUTHORIZED + CONFIRMED webhooks
  POST /payments/{ref}/fail    -- fire FAILED webhook
  GET  /payments             -- list pending intents
```

Full design rationale is in [ARCHITECTURE.md](ARCHITECTURE.md).

## Prerequisites

- Java 21+
- Maven 3.9+

No external infrastructure is needed. The service uses H2 in file mode (`./data/checkout`) in development and H2 in-memory for tests.

## Running

### Main service

```bash
cd checkout-quest
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

### Mock payment provider

```bash
cd mock-payment-provider
mvn spring-boot:run
```

The mock starts on `http://localhost:8081`. When running alongside the main service, payment intents are registered here and webhooks are delivered to the main service automatically when you confirm or fail a payment.

To run only the main service without the mock, remove or blank `mock.provider.url` in `application.yml`. The `MockPaymentGateway` will generate local stub refs instead.

## Tests

```bash
cd checkout-quest
mvn test
```

33 tests across four suites:

| Suite | Count | What it covers |
|---|---|---|
| `ArchitectureRulesTest` | 8 | Bounded-context boundary enforcement (ArchUnit) |
| `OrderStateMachineTest` | 12 | Order domain state machine (pure unit) |
| `PaymentDomainTest` | 9 | Payment domain including terminal-state idempotency (pure unit) |
| `CheckoutIntegrationTest` | 4 | Full HTTP flows: happy path, failure+retry, duplicate webhook, idempotency-key replay |

## Endpoints

### Cart

| Method | Path | Description |
|---|---|---|
| POST | `/carts` | Create a new cart |
| POST | `/carts/{cartId}/items` | Add an item to a cart |
| GET  | `/carts/{cartId}` | Get cart state |
| POST | `/carts/{cartId}/checkout` | Lock the cart and create an order |

**Add item body:**
```json
{ "productId": "prod-1", "quantity": 2, "unitPrice": 49.99, "currency": "USD" }
```

### Orders

| Method | Path | Description |
|---|---|---|
| GET | `/orders/{orderId}` | Get order state |

### Payments

| Method | Path | Description | Headers |
|---|---|---|---|
| POST | `/orders/{orderId}/payment/start` | Initiate a payment | `Idempotency-Key: <uuid>` |
| POST | `/orders/{orderId}/payments/retry` | Retry after failure | `Idempotency-Key: <uuid>` |
| GET  | `/orders/{orderId}/payments` | Get payment for order | |
| POST | `/payments/webhook` | Receive provider webhook | |

**Start payment body:**
```json
{ "amount": 99.98, "currency": "USD" }
```

**Webhook body:**
```json
{ "eventId": "<uuid>", "providerRef": "<ref>", "type": "CONFIRMED" }
```

`type` is one of `AUTHORIZED`, `CONFIRMED`, `FAILED`. For `FAILED`, an optional `reason` field is accepted.

### Ledger (read-only)

| Method | Path | Description |
|---|---|---|
| GET | `/ledger/accounts` | Chart of accounts |
| GET | `/ledger/journal-entries` | All posted journal entries |
| GET | `/ledger/trial-balance` | Trial balance |

## Happy-path flow

```
POST /carts                          -> cartId
POST /carts/{cartId}/items           -> 200
POST /carts/{cartId}/checkout        -> orderId

POST /orders/{orderId}/payment/start -> {providerRef, status}
  (outbox relay fires asynchronously -> order moves to PENDING_PAYMENT)

POST /payments/webhook  {"type":"CONFIRMED", ...}
  (outbox relay fires -> order moves to PAID, ledger entry posted)

GET /orders/{orderId}                -> {status: "PAID"}
```

## Key decisions

**Event sourcing with transactional outbox** — aggregates are stored as event streams in the `events` table. After each save, domain events are published synchronously for in-process projectors and written to the `outbox` table for cross-context integration events. The outbox relay (`OutboxRelay`) delivers integration events every 500 ms (configurable via `scheduling.outbox-relay-interval-ms`).

**Three-layer idempotency** — payment start uses three independent guards:
1. `Idempotency-Key` header cached in the `idempotency_keys` table for HTTP-level replay
2. Partial unique index on `payment_read_model` (PostgreSQL only; skipped in H2 test profile)
3. Deduplication key on the `processed_webhook_events` table for webhook replay

**Separate mock provider** — the mock payment provider is a standalone Spring Boot service on port 8081. The main service calls `POST /payments` on it during payment initiation, and the mock delivers webhooks asynchronously when you trigger confirm/fail. This keeps the production code free of test-only stubs.

**H2 in tests** — the test profile uses H2 in-memory (`jdbc:h2:mem:testdb`). Flyway stops at migration V8 (`flyway.target=8`) to skip the partial unique index in V10, which H2 in-memory does not support. Application-level idempotency checks (layers 1 and 3) still apply in tests.

## Assumptions

- Amounts are stored and compared in the same currency (no FX conversion).
- The chart of accounts is seeded by V8 migration (AR, Revenue, Refunds Payable, Cash).
- `AUTHORIZED` webhooks are optional — a provider may send `CONFIRMED` directly.
- Payment retry is only allowed when the order is in `PAYMENT_FAILED` state.
- Webhook ordering follows the policy in `WebhookOrderingPolicy`: CONFIRMED and FAILED after INITIATED; AUTHORIZED after INITIATED, CONFIRMED/FAILED after AUTHORIZED.
