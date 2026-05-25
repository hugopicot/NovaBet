package com.polymarket.dao;

import com.polymarket.model.Game;
import com.polymarket.model.GameType;
import com.polymarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    private final Connection connection;

    public GameDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public GameDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Game> findAll() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT id, name, type FROM games ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                games.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public Game findById(long id) {
        String sql = "SELECT id, name, type FROM games WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Game> findByType(GameType type) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT id, name, type FROM games WHERE type = ? ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) games.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    private Game mapRow(ResultSet rs) throws SQLException {
        return new Game(
                rs.getLong("id"),
                rs.getString("name"),
                GameType.valueOf(rs.getString("type"))
        );
    }
}
