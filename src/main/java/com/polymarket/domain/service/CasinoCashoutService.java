package com.polymarket.domain.service;

import com.polymarket.dao.WalletRepository;
import com.polymarket.dao.WalletSnapshot;
import com.polymarket.domain.exception.CasinoCashoutException;
import com.polymarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service de cashout des credits casino vers le wallet réel.
 *
 * Règle métier : transfert autorisé uniquement si la règle 1× wagering est atteinte
 * (déléguée à WageringService.canCashout).
 *
 * Transaction atomique : débit virtual_balance + crédit real_balance + reset wagered_amount.
 * Si une étape échoue, tout est rollback — le wallet reste cohérent.
 */
public class CasinoCashoutService {

    private final Connection connection;
    private final WalletRepository walletRepository;
    private final WageringService wageringService;

    public CasinoCashoutService() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new CasinoCashoutException("Impossible d'ouvrir la connexion DB", e);
        }
        this.walletRepository = new WalletRepository(this.connection);
        this.wageringService = new WageringService(this.connection, this.walletRepository);
    }

    public CasinoCashoutService(Connection connection,
                                WalletRepository walletRepository,
                                WageringService wageringService) {
        this.connection = connection;
        this.walletRepository = walletRepository;
        this.wageringService = wageringService;
    }

    /**
     * Transfère la totalité du solde virtuel (casino credits) vers le solde réel.
     *
     * @return le montant transféré
     * @throws CasinoCashoutException si wagering non atteint, wallet introuvable
     *         ou si la transaction SQL échoue.
     */
    public double cashoutAll(long userId) {
        if (!wageringService.canCashout(userId)) {
            throw new CasinoCashoutException(
                    "Cashout refuse : wagering 1x non atteint ou solde casino nul (user " + userId + ")"
            );
        }

        WalletSnapshot wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            throw new CasinoCashoutException("Wallet introuvable pour l'utilisateur " + userId);
        }
        double amount = wallet.virtualBalance();

        try {
            connection.setAutoCommit(false);

            int debited = walletRepository.debitVirtual(userId, amount);
            if (debited != 1) throw new SQLException("Echec debit virtual_balance");

            int credited = walletRepository.creditReal(userId, amount);
            if (credited != 1) throw new SQLException("Echec credit real_balance");

            int reset = walletRepository.resetWagered(userId);
            if (reset != 1) throw new SQLException("Echec reset wagered_amount");

            connection.commit();
            return amount;
        } catch (SQLException e) {
            rollbackQuietly();
            throw new CasinoCashoutException("Transaction cashoutAll echouee — rollback", e);
        } finally {
            restoreAutoCommit();
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
