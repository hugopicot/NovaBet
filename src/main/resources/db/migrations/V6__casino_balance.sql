ALTER TABLE wallets
    ADD COLUMN casino_balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00
    AFTER virtual_balance;

ALTER TABLE wallets
    ADD COLUMN wagered_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00
    AFTER casino_balance;
