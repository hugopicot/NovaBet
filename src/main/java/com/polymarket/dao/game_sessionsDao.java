package com.polymarket.dao;



import com.polymarket.model.game_sessions;
import com.polymarket.util.DatabaseConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class game_sessionsDao {

    private Connection connection;

    public  game_sessionsDao() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    // GET ALL
    public List<game_sessions> getAll() {
        List<game_sessions> list = new ArrayList<>();
        String sql = "SELECT * FROM game_sessions";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                game_sessions gs = new game_sessions(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getLong("game_id"),
                        rs.getDouble("bet_amount"),
                        rs.getString("result"),
                        rs.getDouble("win_amount"),
                        rs.getString("created_at")
                );
                list.add(gs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // FIND BY ID
    public game_sessions findById(Long id) {
        String sql = "SELECT * FROM game_sessions WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new game_sessions(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getLong("game_id"),
                        rs.getDouble("bet_amount"),
                        rs.getString("result"),
                        rs.getDouble("win_amount"),
                        rs.getString("created_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // ADD
    public void add(game_sessions gs) {
        String sql = "INSERT INTO game_sessions (user_id, game_id, bet_amount, result, win_amount, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, gs.getUserId());
            ps.setLong(2, gs.getGameId());
            ps.setDouble(3, gs.getBetAmount());
            ps.setString(4, gs.getResult());
            ps.setDouble(5, gs.getWinAmount());
            ps.setString(6, gs.getCreatedAt());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE
    public void update(game_sessions gs) {
        String sql = "UPDATE game_sessions SET user_id=?, game_id=?, bet_amount=?, result=?, win_amount=?, created_at=? WHERE id=?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, gs.getUserId());
            ps.setLong(2, gs.getGameId());
            ps.setDouble(3, gs.getBetAmount());
            ps.setString(4, gs.getResult());
            ps.setDouble(5, gs.getWinAmount());
            ps.setString(6, gs.getCreatedAt());
            ps.setLong(7, gs.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void delete(Long id) {
        String sql = "DELETE FROM game_sessions WHERE id=?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}