package com.polymarket.model;

public class bets {
    private int id;
    private int user_id;
    private int outcome_id;
    private int amount;
    private int potential_win;

    public bets (int id, int user_id, int outcome_id, int amount, int potential_win){
        this.id=id;
        this.user_id=user_id;
        this.outcome_id=outcome_id;
        this.amount=amount;
        this.potential_win=potential_win;
    }

    public bets(int user_id, int outcome_id, int amount, int potential_win) {
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

    public int getAmount() {
        return amount;
    }

    public int getPotential_win() {
        return potential_win;
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

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPotential_win(int potential_win) {
        this.potential_win = potential_win;
    }

    @Override
    public String toString() {
        return "bets{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", outcome_id=" + outcome_id +
                ", amount=" + amount +
                ", potential_win=" + potential_win +
                '}';
    }
}
