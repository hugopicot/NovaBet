package com.novabet.app.services;

import com.novabet.infrastructure.db.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public long registerUser(String email, String password) throws SQLException {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            String username = email.split("@")[0];
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hash);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                long userId = keys.getLong(1);
                createWallet(conn, userId);
                return userId;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062)
                return -1;
            throw e;
        }
        return -1;
    }

    public long loginUser(String email, String password) throws SQLException {
        String sql = "SELECT id, password_hash FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && BCrypt.checkpw(password, rs.getString("password_hash"))) {
                return rs.getLong("id");
            }
        }
        return -1;
    }

    private void createWallet(Connection conn, long userId) throws SQLException {
        String sql = "INSERT INTO wallets (user_id, real_balance, virtual_balance) VALUES (?, 0.00, 0.00)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }
}
