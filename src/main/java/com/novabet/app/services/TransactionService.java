package com.novabet.app.services;

import com.novabet.infrastructure.db.DatabaseManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionService {


    public long createPendingDeposit(long userId, double amount, String stripeSessionId) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, type, amount, status, stripe_session_id) "
                + "VALUES (?, 'deposit', ?, 'pending', ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);
            stmt.setBigDecimal(2, BigDecimal.valueOf(amount));
            stmt.setString(3, stripeSessionId);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getLong(1);
        }
        return -1;
    }


    public void confirmDeposit(long transactionId, long userId, double amount, String paymentMethod) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                updateTransaction(conn, transactionId, paymentMethod);
                updateWallet(conn, userId, amount);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void markFailed(long transactionId) throws SQLException {
        String sql = "UPDATE transactions SET status = 'failed' WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            stmt.executeUpdate();
        }
    }

    private void updateTransaction(Connection conn, long transactionId, String paymentMethod) throws SQLException {
        String sql = "UPDATE transactions SET status = 'succeeded', payment_method = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentMethod);
            stmt.setLong(2, transactionId);
            stmt.executeUpdate();
        }
    }

    private void updateWallet(Connection conn, long userId, double amount) throws SQLException {
        String sql = "UPDATE wallets SET virtual_balance = virtual_balance + ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, BigDecimal.valueOf(amount));
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }
}
