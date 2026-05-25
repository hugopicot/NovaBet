package com.polymarket.dao;

import com.polymarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Accès BDD aux opérations du wallet utilisées par le casino lobby.
 *
 * Volontairement séparé du walletsDao existant pour ne pas perturber le code
 * Kahina et pour exposer une API propre (PascalCase, Connection injectable
 * pour les transactions atomiques).
 */
public class WalletRepository {

    private final Connection connection;

    public WalletRepository() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public WalletRepository(Connection connection) {
        this.connection = connection;
    }

    public WalletSnapshot findByUserId(long userId) {
        String sql = "SELECT user_id, virtual_balance, wagered_amount " +
                "FROM wallets WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new WalletSnapshot(
                            rs.getLong("user_id"),
                            rs.getDouble("virtual_balance"),
                            rs.getDouble("wagered_amount")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getRealBalance(long userId) {
        String sql = "SELECT real_balance FROM wallets WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("real_balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Débite le solde virtuel (casino credits). Retourne le nombre de lignes affectées. */
    public int debitVirtual(long userId, double amount) throws SQLException {
        String sql = "UPDATE wallets SET virtual_balance = virtual_balance - ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        }
    }

    /** Crédite le solde virtuel (gains de jeu). */
    public int creditVirtual(long userId, double amount) throws SQLException {
        String sql = "UPDATE wallets SET virtual_balance = virtual_balance + ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        }
    }

    /** Crédite le solde réel (cashout depuis le casino). */
    public int creditReal(long userId, double amount) throws SQLException {
        String sql = "UPDATE wallets SET real_balance = real_balance + ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        }
    }

    /** Incrémente le compteur de wagering. */
    public int incrementWagered(long userId, double amount) throws SQLException {
        String sql = "UPDATE wallets SET wagered_amount = wagered_amount + ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, userId);
            return ps.executeUpdate();
        }
    }

    /** Remet à zéro le compteur de wagering (à appeler après un cashout réussi). */
    public int resetWagered(long userId) throws SQLException {
        String sql = "UPDATE wallets SET wagered_amount = 0 WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate();
        }
    }
}
