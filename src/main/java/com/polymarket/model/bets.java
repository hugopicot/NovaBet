package com.polymarket.model;

public class bets {
    private int id;
    private int user_id;
    private int outcome_id;
    private double amount;
    private double potential_win;
    private Double payout;
    private String settledAt;

    public bets(int id, int user_id, int outcome_id, double amount, double potential_win) {
        this.id = id;
        this.user_id = user_id;
        this.outcome_id = outcome_id;
        this.amount = amount;
        this.potential_win = potential_win;
    }

    public bets(int user_id, int outcome_id, double amount, double potential_win) {
        this.user_id = user_id;
        this.outcome_id = outcome_id;
        this.amount = amount;
        this.potential_win = potential_win;
    }

    public bets() {
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getOutcome_id() {
        return outcome_id;
    }

    public double getAmount() {
        return amount;
    }

    public double getPotential_win() {
        return potential_win;
    }

    public Double getPayout() {
        return payout;
    }

    public String getSettledAt() {
        return settledAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setOutcome_id(int outcome_id) {
        this.outcome_id = outcome_id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPotential_win(double potential_win) {
        this.potential_win = potential_win;
    }

    public void setPayout(Double payout) {
        this.payout = payout;
    }

    public void setSettledAt(String settledAt) {
        this.settledAt = settledAt;
    }

    @Override
    public String toString() {
        return "bets{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", outcome_id=" + outcome_id +
                ", amount=" + amount +
                ", potential_win=" + potential_win +
                ", payout=" + payout +
                '}';
    }
}
