-- Chart of Accounts seeded at startup
-- 1100: Cash/Bank - receives confirmed payment funds
-- 4000: Revenue - recognized on payment confirmation
-- 5000: Transaction Expense - cost of failed/processing fees
INSERT INTO ledger_account (account_id, code, name, type, normal_balance, balance_amount, currency, version)
VALUES
    ('00000000-0000-7000-8000-000000001100', '1100', 'Cash',                 'ASSET',   'DEBIT',  0.00, 'USD', 0),
    ('00000000-0000-7000-8000-000000004000', '4000', 'Revenue',              'REVENUE', 'CREDIT', 0.00, 'USD', 0),
    ('00000000-0000-7000-8000-000000005000', '5000', 'Transaction Expense',  'EXPENSE', 'DEBIT',  0.00, 'USD', 0);
