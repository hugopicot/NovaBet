-- ─────────────────────────────────────────────────────────────────────
-- Migration : casino lobby
-- Ajoute le compteur de wagering au wallet + seed les 2 jeux du lobby.
-- Script idempotent : peut être ré-exécuté sans erreur.
-- ─────────────────────────────────────────────────────────────────────

-- 1. Compteur de wagering pour la règle 1× (cashout des casino credits)
--    virtual_balance représente les casino credits.
--    wagered_amount cumule les bets depuis le dernier cashout.
--    Cashout autorisé quand wagered_amount >= virtual_balance initial.
--    Note : MySQL ne supporte pas IF NOT EXISTS sur ADD COLUMN.
--    A ne pas ré-exécuter (erreur 1060 "Duplicate column" si déjà présent).
ALTER TABLE wallets
    ADD COLUMN wagered_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00
    AFTER virtual_balance;

-- 2. Seed des 2 jeux du lobby (idempotent via NOT EXISTS)
--    types : SLOT (machine à sous), CRASH (jeu de crash type Aviator)
INSERT INTO games (name, type)
SELECT 'Diamond Slots', 'SLOT'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = 'Diamond Slots');

INSERT INTO games (name, type)
SELECT 'Crash Rocket', 'CRASH'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = 'Crash Rocket');
