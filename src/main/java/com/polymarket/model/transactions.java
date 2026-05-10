package com.polymarket.model;

public class transactions {
    private Long id;
    private Long userId;
    private String type;
    private double amount;
    private String createdAt;

    public transactions(Long id, Long userId, String type, double amount, String createdAt){
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public transactions(Long userId, String type, double amount, String createdAt) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public transactions() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "transactions{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
}
