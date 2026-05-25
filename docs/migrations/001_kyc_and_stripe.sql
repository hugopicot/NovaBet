-- Migration 001 — KYC + Stripe Checkout
-- À exécuter une seule fois sur la base `novabet`

ALTER TABLE `users`
    ADD COLUMN `kyc_status` ENUM('pending','processing','verified','rejected')
        NOT NULL DEFAULT 'pending' AFTER `password_hash`,
    ADD COLUMN `stripe_verification_session_id` VARCHAR(255) NULL AFTER `kyc_status`;

ALTER TABLE `transactions`
    ADD COLUMN `status` ENUM('pending','succeeded','failed') NOT NULL DEFAULT 'pending' AFTER `amount`,
    ADD COLUMN `stripe_session_id` VARCHAR(255) NULL AFTER `status`,
    ADD COLUMN `payment_method` VARCHAR(50) NULL AFTER `stripe_session_id`;
