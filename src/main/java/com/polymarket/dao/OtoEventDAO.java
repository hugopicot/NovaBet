package com.polymarket.dao;

import com.polymarket.model.OtoEvent;
import com.polymarket.model.OtoEventType;
import com.polymarket.model.OtoVariant;
import com.polymarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class OtoEventDAO {

    private final Connection connection;

    public OtoEventDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public OtoEventDAO(Connection connection) {
        this.connection = connection;
    }

    public int save(OtoEvent event) {
        String sql = "INSERT INTO oto_events " +
                "(user_id, variant, event_type, base_amount, multiplier, payout_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, event.getUserId());
            ps.setString(2, event.getVariant().name());
            ps.setString(3, event.getEventType().name());
            ps.setDouble(4, event.getBaseAmount());

            if (event.getMultiplier() != null) {
                ps.setInt(5, event.getMultiplier());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (event.getPayoutAmount() != null) {
                ps.setDouble(6, event.getPayoutAmount());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    event.setId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<OtoEvent> findByUser(int userId) {
        List<OtoEvent> events = new ArrayList<>();
        String sql = "SELECT id, user_id, variant, event_type, base_amount, " +
                "multiplier, payout_amount, created_at " +
                "FROM oto_events WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public int countByEventType(OtoEventType type) {
        String sql = "SELECT COUNT(*) FROM oto_events WHERE event_type = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByVariantAndType(OtoVariant variant, OtoEventType type) {
        String sql = "SELECT COUNT(*) FROM oto_events WHERE variant = ? AND event_type = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, variant.name());
            ps.setString(2, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private OtoEvent mapRow(ResultSet rs) throws SQLException {
        Integer multiplier = rs.getObject("multiplier") == null ? null : rs.getInt("multiplier");
        Double payout = rs.getObject("payout_amount") == null ? null : rs.getDouble("payout_amount");
        Timestamp ts = rs.getTimestamp("created_at");

        return new OtoEvent(
                rs.getInt("id"),
                rs.getInt("user_id"),
                OtoVariant.valueOf(rs.getString("variant")),
                OtoEventType.valueOf(rs.getString("event_type")),
                rs.getDouble("base_amount"),
                multiplier,
                payout,
                ts == null ? null : ts.toLocalDateTime()
        );
    }
}
