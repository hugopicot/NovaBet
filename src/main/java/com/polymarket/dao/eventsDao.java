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

    // Récupérer tous les events
    public List<events> getAll() {
        List<events> list = new ArrayList<>();
        String sql = "SELECT * FROM events";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                events event = new events(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("resolution"),
                        rs.getString("created_at")
                );
                list.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer un event par ID
    public events findById(Long id) {
        String sql = "SELECT * FROM events WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new events(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("resolution"),
                        rs.getString("created_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ajouter un event
    public void add(events event) {

        String sql = "INSERT INTO events (title, description, status, resolution, created_at) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getStatus());
            ps.setString(4, event.getResolution());
            ps.setString(5, event.getCreatedAt());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                event.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier un event
    public void update(events event) {
        String sql = "UPDATE events SET title = ?, description = ?, status = ?, resolution = ?, created_at = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getStatus());
            ps.setString(4, event.getResolution());
            ps.setString(5, event.getCreatedAt());
            ps.setLong(6, event.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un event
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