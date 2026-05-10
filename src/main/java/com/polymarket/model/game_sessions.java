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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
    }

    public Long getGameId() {
        return gameId;
    }


    public void setGameId(Long gameId) {
    }
    public double getBetAmount() {
        return betAmount;
    }
    public void setBetAmount(double betAmount) {}
    public String getResult() {
        return result;
    }
    public void setResult(String result) {}
    public double getWinAmount() {
        return winAmount;
    }
   public void setWinAmount(double winAmount) {}
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {}

}