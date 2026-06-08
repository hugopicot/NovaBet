package com.polymarket.dao;

import com.polymarket.model.bets;
import com.polymarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class betsDao {

    private Connection connection;

    public betsDao() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    public betsDao(Connection connection) {
        this.connection = connection;
    }

    private bets mapRow(ResultSet rs) throws SQLException {
        bets bet = new bets(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("outcome_id"),
                rs.getDouble("amount"),
                rs.getDouble("potential_win")
        );
        double payout = rs.getDouble("payout");
        if (!rs.wasNull()) {
            bet.setPayout(payout);
        }
        bet.setSettledAt(rs.getString("settled_at"));
        return bet;
    }

    public List<bets> getAll() {
        List<bets> list = new ArrayList<>();
        String sql = "SELECT * FROM bets";

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

    public bets findById(int id) {
        String sql = "SELECT * FROM bets WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void add(bets bet) {
        String sql = "INSERT INTO bets (user_id, outcome_id, amount, potential_win) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, bet.getUser_id());
            ps.setInt(2, bet.getOutcome_id());
            ps.setDouble(3, bet.getAmount());
            ps.setDouble(4, bet.getPotential_win());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                bet.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(bets bet) {
        String sql = "UPDATE bets SET user_id = ?, outcome_id = ?, amount = ?, potential_win = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bet.getUser_id());
            ps.setInt(2, bet.getOutcome_id());
            ps.setDouble(3, bet.getAmount());
            ps.setDouble(4, bet.getPotential_win());
            ps.setInt(5, bet.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM bets WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<bets> findByUserId(int userId) {
        List<bets> list = new ArrayList<>();
        String sql = "SELECT * FROM bets WHERE user_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<bets> findByOutcomeId(int outcomeId) {
        List<bets> list = new ArrayList<>();
        String sql = "SELECT * FROM bets WHERE outcome_id = ? AND settled_at IS NULL";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, outcomeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<bets> findUnsettledByEventId(Long eventId) {
        List<bets> list = new ArrayList<>();
        String sql = "SELECT b.* FROM bets b JOIN outcomes o ON b.outcome_id = o.id WHERE o.event_id = ? AND b.settled_at IS NULL";

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

    public void settleBet(int betId, double payout) {
        String sql = "UPDATE bets SET payout = ?, settled_at = NOW() WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, payout);
            ps.setInt(2, betId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
