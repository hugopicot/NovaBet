package com.polymarket.dao;

import com.polymarket.model.games;
import com.polymarket.util.DatabaseConnection;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class gamesDao  {

    private Connection connection;

    public gamesDao()   throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    // Récupérer tous les jeux
    public List<games> getAll() {
        List<games> list = new ArrayList<>();
        String sql = "SELECT * FROM games";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                games game = new games(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("type")
                );
                list.add(game);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Récupérer un jeu par son ID
    public games findById(Long id) {
        String sql = "SELECT * FROM games WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new games(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("type")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ajouter un nouveau jeu
    public void add(games game) {
        String sql = "INSERT INTO games (name, type) VALUES (?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, game.getName());
            ps.setString(2, game.getType());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifier un jeu
    public void update(games game) {
        String sql = "UPDATE games SET name = ?, type = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, game.getName());
            ps.setString(2, game.getType());
            ps.setLong(3, game.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un jeu
    public void delete(Long id) {
        String sql = "DELETE FROM games WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}