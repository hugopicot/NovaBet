package com.polymarket.dao;

import com.polymarket.model.PriceHistory;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PriceHistoryDao {

    private Connection connection;

    public PriceHistoryDao() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    public PriceHistoryDao(Connection connection) {
        this.connection = connection;
    }

    private PriceHistory mapRow(ResultSet rs) throws SQLException {
        return new PriceHistory(
                rs.getLong("id"),
                rs.getLong("event_id"),
                rs.getLong("outcome_id"),
                rs.getDouble("odds"),
                rs.getString("recorded_at")
        );
    }

    public void recordPrice(Long eventId, Long outcomeId, double odds) {
        String sql = "INSERT INTO price_history (event_id, outcome_id, odds, recorded_at) VALUES (?, ?, ?, NOW())";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, eventId);
            ps.setLong(2, outcomeId);
            ps.setDouble(3, odds);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PriceHistory> findByEventId(Long eventId) {
        List<PriceHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM price_history WHERE event_id = ? ORDER BY recorded_at ASC";

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

    public List<PriceHistory> findByEventIdAndOutcomeId(Long eventId, Long outcomeId) {
        List<PriceHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM price_history WHERE event_id = ? AND outcome_id = ? ORDER BY recorded_at ASC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, eventId);
            ps.setLong(2, outcomeId);
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
