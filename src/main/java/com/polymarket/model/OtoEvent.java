package com.polymarket.model;

import java.time.LocalDateTime;

public class OtoEvent {

    private int id;
    private int userId;
    private OtoVariant variant;
    private OtoEventType eventType;
    private double baseAmount;
    private Integer multiplier;
    private Double payoutAmount;
    private LocalDateTime createdAt;

    public OtoEvent() {
    }

    public OtoEvent(int userId, OtoVariant variant, OtoEventType eventType,
                    double baseAmount, Integer multiplier, Double payoutAmount) {
        this.userId = userId;
        this.variant = variant;
        this.eventType = eventType;
        this.baseAmount = baseAmount;
        this.multiplier = multiplier;
        this.payoutAmount = payoutAmount;
    }

    public OtoEvent(int id, int userId, OtoVariant variant, OtoEventType eventType,
                    double baseAmount, Integer multiplier, Double payoutAmount,
                    LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.variant = variant;
        this.eventType = eventType;
        this.baseAmount = baseAmount;
        this.multiplier = multiplier;
        this.payoutAmount = payoutAmount;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public OtoVariant getVariant() { return variant; }
    public void setVariant(OtoVariant variant) { this.variant = variant; }

    public OtoEventType getEventType() { return eventType; }
    public void setEventType(OtoEventType eventType) { this.eventType = eventType; }

    public double getBaseAmount() { return baseAmount; }
    public void setBaseAmount(double baseAmount) { this.baseAmount = baseAmount; }

    public Integer getMultiplier() { return multiplier; }
    public void setMultiplier(Integer multiplier) { this.multiplier = multiplier; }

    public Double getPayoutAmount() { return payoutAmount; }
    public void setPayoutAmount(Double payoutAmount) { this.payoutAmount = payoutAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "OtoEvent{" +
                "id=" + id +
                ", userId=" + userId +
                ", variant=" + variant +
                ", eventType=" + eventType +
                ", baseAmount=" + baseAmount +
                ", multiplier=" + multiplier +
                ", payoutAmount=" + payoutAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
