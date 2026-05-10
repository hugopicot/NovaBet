package com.polymarket.dao;

import com.polymarket.model.transactions;
import com.polymarket.util.DatabaseConnection;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class transactionsDao {

    private Connection connection;

    public transactionsDao() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    // Récupérer toutes les transactions
    public List<transactions> getAll() {
        List<transactions> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                transactions transaction = new transactions(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("created_at")
                );
                list.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer une transaction par son ID
    public transactions findById(Long id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new transactions(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("created_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ajouter une nouvelle transaction
    public void add(transactions transaction) {
        String sql = "INSERT INTO transactions (user_id, type, amount, created_at) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, transaction.getUserId());
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setString(4, transaction.getCreatedAt());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier une transaction
    public void update(transactions transaction) {
        String sql = "UPDATE transactions SET user_id = ?, type = ?, amount = ?, created_at = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, transaction.getUserId());
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setString(4, transaction.getCreatedAt());
            ps.setLong(5, transaction.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer une transaction
    public void delete(Long id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}