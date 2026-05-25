package com.polymarket.dao;

import com.polymarket.model.users;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    public class usersDao {

        private Connection connection;

        public usersDao() throws SQLException {
            connection = DatabaseConnection.getConnection();
        }

    private users mapUser(ResultSet rs) throws SQLException {
        users user = new users(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("created_at")
        );
        try { user.setStripeVerificationSessionId(rs.getString("stripe_verification_session_id")); } catch (SQLException ignored) {}
        try { user.setKycStatus(rs.getString("kyc_status")); } catch (SQLException ignored) {}
        return user;
    }

    // Récupérer tous les utilisateurs
    public List<users> getAll() {
        List<users> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(mapUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer un utilisateur par son ID
    public users findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

        // Ajouter un nouvel utilisateur
        public void add(users user) {
            String sql = "INSERT INTO users (username, email, password_hash, created_at) VALUES (?, ?, ?, ?)";

            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPasswordHash());
                ps.setString(4, user.getCreatedAt());

                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Modifier un utilisateur
        public void update(users user) {
            String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, created_at = ? WHERE id = ?";

            try {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPasswordHash());
                ps.setString(4, user.getCreatedAt());
                ps.setLong(5, user.getId());

                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

// Supprimer un utilisateur
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer un utilisateur par email
    public users findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setKycSession(Long userId, String stripeSessionId) {
        String sql = "UPDATE users SET stripe_verification_session_id = ?, kyc_status = 'processing' WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, stripeSessionId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setKycStatus(Long userId, String kycStatus) {
        String sql = "UPDATE users SET kyc_status = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, kycStatus);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getKycStatus(Long userId) {
        String sql = "SELECT kyc_status FROM users WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("kyc_status");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "pending";
    }
}