package com.polymarket.dao;

import com.polymarket.model.events;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class eventsDao {

    private Connection connection;

    public eventsDao() throws SQLException  {
        connection = DatabaseConnection.getConnection();
    }

    public eventsDao(Connection connection) {
        this.connection = connection;
    }

    private events mapRow(ResultSet rs) throws SQLException {
        return new events(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getString("resolution"),
                rs.getString("created_at"),
                rs.getString("polymarket_id"),
                rs.getString("polymarket_condition_id"),
                rs.getString("source"),
                rs.getString("end_date"),
                rs.getString("image_url")
        );
    }

    public List<events> getAll() {
        List<events> list = new ArrayList<>();
        String sql = "SELECT * FROM events";

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

    public events findById(Long id) {
        String sql = "SELECT * FROM events WHERE id = ?";

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

    public events findByPolymarketId(String polymarketId) {
        String sql = "SELECT * FROM events WHERE polymarket_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, polymarketId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<events> findOpenBySource(String source) {
        List<events> list = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE source = ? AND status = 'OPEN'";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, source);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<events> findClosedUnresolved() {
        List<events> list = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE status = 'OPEN' AND source = 'POLYMARKET' AND polymarket_id IS NOT NULL";

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

    public void add(events event) {

        String sql = "INSERT INTO events (title, description, status, resolution, created_at, polymarket_id, polymarket_condition_id, source, end_date, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getStatus());
            ps.setString(4, event.getResolution());
            ps.setString(5, event.getCreatedAt());
            ps.setString(6, event.getPolymarketId());
            ps.setString(7, event.getPolymarketConditionId());
            ps.setString(8, event.getSource());
            ps.setString(9, event.getEndDate());
            ps.setString(10, event.getImageUrl());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(events event) {
        String sql = "UPDATE events SET title = ?, description = ?, status = ?, resolution = ?, created_at = ?, polymarket_id = ?, polymarket_condition_id = ?, source = ?, end_date = ?, image_url = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getStatus());
            ps.setString(4, event.getResolution());
            ps.setString(5, event.getCreatedAt());
            ps.setString(6, event.getPolymarketId());
            ps.setString(7, event.getPolymarketConditionId());
            ps.setString(8, event.getSource());
            ps.setString(9, event.getEndDate());
            ps.setString(10, event.getImageUrl());
            ps.setLong(11, event.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM events WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
