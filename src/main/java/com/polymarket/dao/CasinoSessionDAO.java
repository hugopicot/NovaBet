package com.polymarket.dao;

import com.polymarket.model.CasinoSession;
import com.polymarket.model.CasinoSessionStatus;
import com.polymarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

public class CasinoSessionDAO {

    private final Connection connection;

    public CasinoSessionDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public CasinoSessionDAO(Connection connection) {
        this.connection = connection;
    }

    public int save(CasinoSession session) {
        String sql = "INSERT INTO casino_sessions " +
                "(user_id, credits_in, credits_out, oto_multiplier, wagered, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, session.getUserId());
            ps.setDouble(2, session.getCreditsIn());
            ps.setDouble(3, session.getCreditsOut());

            if (session.getOtoMultiplier() != null) {
                ps.setInt(4, session.getOtoMultiplier());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setDouble(5, session.getWagered());
            ps.setString(6, session.getStatus().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    session.setId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public CasinoSession findById(int id) {
        String sql = "SELECT id, user_id, credits_in, credits_out, oto_multiplier, " +
                "wagered, status, created_at FROM casino_sessions WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CasinoSession findActiveByUser(int userId) {
        String sql = "SELECT id, user_id, credits_in, credits_out, oto_multiplier, " +
                "wagered, status, created_at FROM casino_sessions " +
                "WHERE user_id = ? AND status = 'OPEN' " +
                "ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateWagered(int sessionId, double newWagered) {
        String sql = "UPDATE casino_sessions SET wagered = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, newWagered);
            ps.setInt(2, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(int sessionId, double creditsOut) {
        String sql = "UPDATE casino_sessions SET status = 'CLOSED', credits_out = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, creditsOut);
            ps.setInt(2, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private CasinoSession mapRow(ResultSet rs) throws SQLException {
        Integer multiplier = rs.getObject("oto_multiplier") == null ? null : rs.getInt("oto_multiplier");
        Timestamp ts = rs.getTimestamp("created_at");

        return new CasinoSession(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getDouble("credits_in"),
                rs.getDouble("credits_out"),
                multiplier,
                rs.getDouble("wagered"),
                CasinoSessionStatus.valueOf(rs.getString("status")),
                ts == null ? null : ts.toLocalDateTime()
        );
    }
}
