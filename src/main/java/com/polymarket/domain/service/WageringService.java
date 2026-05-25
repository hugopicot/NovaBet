package com.polymarket.domain.service;

import com.polymarket.dao.WalletRepository;
import com.polymarket.dao.WalletSnapshot;
import com.polymarket.domain.exception.WageringServiceException;
import com.polymarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service métier de la règle 1× wagering du casino.
 *
 * - canCashout : vrai si l'utilisateur a misé au moins l'équivalent
 *   de son solde casino actuel ET qu'il a un solde à retirer.
 * - recordBet : transaction atomique débit virtual_balance + incrément wagered_amount.
 * - recordWin : crédit virtual_balance (les gains ne comptent pas dans le wagering,
 *   règle standard des bonus casino).
 */
public class WageringService {

    private final Connection connection;
    private final WalletRepository walletRepository;

    public WageringService() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new WageringServiceException("Impossible d'ouvrir la connexion DB", e);
        }
        this.walletRepository = new WalletRepository(this.connection);
    }

    public WageringService(Connection connection, WalletRepository walletRepository) {
        this.connection = connection;
        this.walletRepository = walletRepository;
    }

    /**
     * L'utilisateur peut-il cashout ses credits casino vers son solde réel ?
     * <p>
     * Règle 1× : il faut avoir misé au moins autant que son solde virtuel actuel,
     * ET avoir un solde > 0 à transférer.
     */
    public boolean canCashout(long userId) {
        WalletSnapshot wallet = walletRepository.findByUserId(userId);
        if (wallet == null) return false;
        return wallet.virtualBalance() > 0
                && wallet.wageredAmount() >= wallet.virtualBalance();
    }

    /**
     * Enregistre une mise : débit du solde virtuel + incrément du compteur de wagering.
     * Transaction atomique — soit les deux opérations passent, soit aucune.
     *
     * @throws WageringServiceException si montant ≤ 0, wallet introuvable,
     *         credits insuffisants, ou si la transaction SQL échoue.
     */
    public void recordBet(long userId, double amount) {
        if (amount <= 0) {
            throw new WageringServiceException("Le montant de mise doit etre strictement positif : " + amount);
        }
        WalletSnapshot wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            throw new WageringServiceException("Wallet introuvable pour l'utilisateur " + userId);
        }
        if (wallet.virtualBalance() < amount) {
            throw new WageringServiceException("Credits casino insuffisants : solde=" + wallet.virtualBalance()
                    + ", mise=" + amount);
        }

        try {
            connection.setAutoCommit(false);
            int debited = walletRepository.debitVirtual(userId, amount);
            if (debited != 1) throw new SQLException("Echec debit virtual_balance");
            int incremented = walletRepository.incrementWagered(userId, amount);
            if (incremented != 1) throw new SQLException("Echec increment wagered_amount");
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly();
            throw new WageringServiceException("Transaction recordBet echouee — rollback", e);
        } finally {
            restoreAutoCommit();
        }
    }

    /**
     * Enregistre un gain de jeu : crédit du solde virtuel.
     * Les gains ne comptent pas dans le wagering (règle standard des bonus casino).
     */
    public void recordWin(long userId, double amount) {
        if (amount <= 0) {
            throw new WageringServiceException("Le montant de gain doit etre strictement positif : " + amount);
        }
        try {
            int credited = walletRepository.creditVirtual(userId, amount);
            if (credited != 1) {
                throw new WageringServiceException("Wallet introuvable pour l'utilisateur " + userId);
            }
        } catch (SQLException e) {
            throw new WageringServiceException("Echec credit virtual_balance", e);
        }
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void restoreAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
    }
}
