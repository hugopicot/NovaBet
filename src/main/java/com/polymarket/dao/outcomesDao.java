package com.polymarket.dao;

import com.polymarket.model.outcomes;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class outcomesDao {

    private Connection connection;

    public outcomesDao() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    // Récupérer tous les outcomes
    public List<outcomes> getAll() {
        List<outcomes> list = new ArrayList<>();
        String sql = "SELECT * FROM outcomes";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                outcomes outcome = new outcomes(
                        rs.getLong("id"),
                        rs.getLong("event_id"),
                        rs.getString("label"),
                        rs.getDouble("odds")
                );
                list.add(outcome);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer un outcome par son ID
    public outcomes findById(Long id) {
        String sql = "SELECT * FROM outcomes WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new outcomes(
                        rs.getLong("id"),
                        rs.getLong("event_id"),
                        rs.getString("label"),
                        rs.getDouble("odds")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ajouter un nouvel outcome
    public void add(outcomes outcome) {
        String sql = "INSERT INTO outcomes (event_id, label, odds) VALUES (?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, outcome.getEventId());
            ps.setString(2, outcome.getLabel());
            ps.setDouble(3, outcome.getOdds());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier un outcome
    public void update(outcomes outcome) {
        String sql = "UPDATE outcomes SET event_id = ?, label = ?, odds = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, outcome.getEventId());
            ps.setString(2, outcome.getLabel());
            ps.setDouble(3, outcome.getOdds());
            ps.setLong(4, outcome.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un outcome
    public void delete(Long id) {
        String sql = "DELETE FROM outcomes WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}