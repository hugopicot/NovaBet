package com.novabet.app.services;

import com.novabet.infrastructure.db.DatabaseManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionService {

    public void createDeposit(long userId, double amount) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                insertTransaction(conn, userId, amount);
                updateWallet(conn, userId, amount);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private void insertTransaction(Connection conn, long userId, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'deposit', ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setBigDecimal(2, BigDecimal.valueOf(amount));
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
