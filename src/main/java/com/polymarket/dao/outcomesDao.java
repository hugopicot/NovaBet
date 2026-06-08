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

    public outcomesDao(Connection connection) {
        this.connection = connection;
    }

    private outcomes mapRow(ResultSet rs) throws SQLException {
        return new outcomes(
                rs.getLong("id"),
                rs.getLong("event_id"),
                rs.getString("label"),
                rs.getDouble("odds"),
                rs.getString("polymarket_token_id")
        );
    }

    public List<outcomes> getAll() {
        List<outcomes> list = new ArrayList<>();
        String sql = "SELECT * FROM outcomes";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public outcomes findById(Long id) {
        String sql = "SELECT * FROM outcomes WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public outcomes findByPolymarketTokenId(String tokenId) {
        String sql = "SELECT * FROM outcomes WHERE polymarket_token_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tokenId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void add(outcomes outcome) {
        String sql = "INSERT INTO outcomes (event_id, label, odds, polymarket_token_id) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, outcome.getEventId());
            ps.setString(2, outcome.getLabel());
            ps.setDouble(3, outcome.getOdds());
            ps.setString(4, outcome.getPolymarketTokenId());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                outcome.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(outcomes outcome) {
        String sql = "UPDATE outcomes SET event_id = ?, label = ?, odds = ?, polymarket_token_id = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, outcome.getEventId());
            ps.setString(2, outcome.getLabel());
            ps.setDouble(3, outcome.getOdds());
            ps.setString(4, outcome.getPolymarketTokenId());
            ps.setLong(5, outcome.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOdds(Long id, double odds) {
        String sql = "UPDATE outcomes SET odds = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, odds);
            ps.setLong(2, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public List<outcomes> findByEventId(Long eventId) {
        List<outcomes> list = new ArrayList<>();
        String sql = "SELECT * FROM outcomes WHERE event_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, eventId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
