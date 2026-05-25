package com.polymarket.dao;

/**
 * Vue partielle d'un wallet pour les besoins du lobby casino :
 * solde de credits virtuels + montant mise depuis le dernier cashout.
 *
 * Lecture seule (record). Pour modifier le wallet, passer par WalletRepository.
 */
public record WalletSnapshot(long userId, double virtualBalance, double wageredAmount) {
}
