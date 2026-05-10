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

        // Récupérer tous les utilisateurs
        public List<users> getAll() {
            List<users> list = new ArrayList<>();
            String sql = "SELECT * FROM users";

            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    users user = new users(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("created_at")
                    );
                    list.add(user);
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
                    return new users(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("created_at")
                    );
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
    }