package com.polymarket.dao;

import com.polymarket.model.bets;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class betsDao {

    private Connection connection;

    public betsDao() throws SQLException {
        connection = DatabaseConnection.getConnection();    }

    // Récupérer tous les paris
    public List<bets> getAll() {
        List<bets> list = new ArrayList<>();
        String sql = "SELECT * FROM bets";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                bets bet = new bets(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("outcome_id"),
                        rs.getInt("amount"),
                        rs.getInt("potential_win")
                );
                list.add(bet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer un pari par son ID
    public bets findById(int id) {
        String sql = "SELECT * FROM bets WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new bets(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("outcome_id"),
                        rs.getInt("amount"),
                        rs.getInt("potential_win")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ajouter un nouveau pari
    public void add(bets bet) {
        String sql = "INSERT INTO bets (user_id, outcome_id, amount, potential_win) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bet.getUser_id());
            ps.setInt(2, bet.getOutcome_id());
            ps.setInt(3, bet.getAmount());
            ps.setInt(4, bet.getPotential_win());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier un pari
    public void update(bets bet) {
        String sql = "UPDATE bets SET user_id = ?, outcome_id = ?, amount = ?, potential_win = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bet.getUser_id());
            ps.setInt(2, bet.getOutcome_id());
            ps.setInt(3, bet.getAmount());
            ps.setInt(4, bet.getPotential_win());
            ps.setInt(5, bet.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un pari
    public void delete(int id) {
        String sql = "DELETE FROM bets WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer les paris par user_id
    public List<bets> findByUserId(int userId) {
        List<bets> list = new ArrayList<>();
        String sql = "SELECT * FROM bets WHERE user_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bets bet = new bets(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("outcome_id"),
                        rs.getInt("amount"),
                        rs.getInt("potential_win")
                );
                list.add(bet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}