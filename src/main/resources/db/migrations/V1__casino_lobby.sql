ALTER TABLE wallets
    ADD COLUMN wagered_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00
    AFTER virtual_balance;

INSERT INTO games (name, type)
SELECT 'Slots Diamant', 'SLOT'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = 'Slots Diamant');

INSERT INTO games (name, type)
SELECT 'Crash Roquette', 'CRASH'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = 'Crash Roquette');
