package com.polymarket.dao;

import com.polymarket.model.wallets;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class walletsDao {

    private Connection connection;

    public walletsDao() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    // Récupérer tous les wallets
    public List<wallets> getAll() {
        List<wallets> list = new ArrayList<>();
        String sql = "SELECT * FROM wallets";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                wallets wallet = new wallets(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getDouble("real_balance"),
                        rs.getDouble("virtual_balance")
                );
                list.add(wallet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer un wallet par son ID
    public wallets findById(Long id) {
        String sql = "SELECT * FROM wallets WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new wallets(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getDouble("real_balance"),
                        rs.getDouble("virtual_balance")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ajouter un nouveau wallet
    public void add(wallets wallet) {
        String sql = "INSERT INTO wallets (user_id, real_balance, virtual_balance) VALUES (?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, wallet.getUserId());
            ps.setDouble(2, wallet.getRealBalance());
            ps.setDouble(3, wallet.getVirtualBalance());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier un wallet
    public void update(wallets wallet) {
        String sql = "UPDATE wallets SET user_id = ?, real_balance = ?, virtual_balance = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, wallet.getUserId());
            ps.setDouble(2, wallet.getRealBalance());
            ps.setDouble(3, wallet.getVirtualBalance());
            ps.setLong(4, wallet.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un wallet
    public void delete(Long id) {
        String sql = "DELETE FROM wallets WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer un wallet par user_id
    public wallets findByUserId(Long userId) {
        String sql = "SELECT * FROM wallets WHERE user_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new wallets(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getDouble("real_balance"),
                        rs.getDouble("virtual_balance")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}