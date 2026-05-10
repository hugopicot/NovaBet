package com.polymarket.model;

public class game_sessions {
    private Long id;
    private Long userId;
    private Long gameId;
    private double betAmount;
    private String result;
    private double winAmount;
    private String createdAt;

    public game_sessions(Long id, Long userId, Long gameId, double betAmount, String result, double winAmount, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.gameId = gameId;
        this.betAmount = betAmount;
        this.result = result;
        this.winAmount = winAmount;
        this.createdAt = createdAt;
    }

    public game_sessions(Long userId, Long gameId, double betAmount, String result, double winAmount, String createdAt) {
        this.userId = userId;
        this.gameId = gameId;
        this.betAmount = betAmount;
        this.result = result;
        this.winAmount = winAmount;
        this.createdAt = createdAt;
    }

    public game_sessions() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public double getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(double winAmount) {
        this.winAmount = winAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "game_sessions{" +
                "id=" + id +
                ", userId=" + userId +
                ", gameId=" + gameId +
                ", betAmount=" + betAmount +
                ", result='" + result + '\'' +
                ", winAmount=" + winAmount +
                '}';
    }
}
