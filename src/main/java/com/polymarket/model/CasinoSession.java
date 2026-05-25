package com.polymarket.model;

import java.time.LocalDateTime;

public class CasinoSession {

    private int id;
    private int userId;
    private double creditsIn;
    private double creditsOut;
    private Integer otoMultiplier;
    private double wagered;
    private CasinoSessionStatus status;
    private LocalDateTime createdAt;

    public CasinoSession() {
    }

    public CasinoSession(int userId, double creditsIn, Integer otoMultiplier) {
        this.userId = userId;
        this.creditsIn = creditsIn;
        this.creditsOut = 0;
        this.otoMultiplier = otoMultiplier;
        this.wagered = 0;
        this.status = CasinoSessionStatus.OPEN;
    }

    public CasinoSession(int id, int userId, double creditsIn, double creditsOut,
                         Integer otoMultiplier, double wagered,
                         CasinoSessionStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.creditsIn = creditsIn;
        this.creditsOut = creditsOut;
        this.otoMultiplier = otoMultiplier;
        this.wagered = wagered;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getCreditsIn() { return creditsIn; }
    public void setCreditsIn(double creditsIn) { this.creditsIn = creditsIn; }

    public double getCreditsOut() { return creditsOut; }
    public void setCreditsOut(double creditsOut) { this.creditsOut = creditsOut; }

    public Integer getOtoMultiplier() { return otoMultiplier; }
    public void setOtoMultiplier(Integer otoMultiplier) { this.otoMultiplier = otoMultiplier; }

    public double getWagered() { return wagered; }
    public void setWagered(double wagered) { this.wagered = wagered; }

    public CasinoSessionStatus getStatus() { return status; }
    public void setStatus(CasinoSessionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "CasinoSession{" +
                "id=" + id +
                ", userId=" + userId +
                ", creditsIn=" + creditsIn +
                ", creditsOut=" + creditsOut +
                ", otoMultiplier=" + otoMultiplier +
                ", wagered=" + wagered +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
