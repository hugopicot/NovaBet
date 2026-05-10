package com.polymarket.model;

public class wallets {
    private Long id;
    private Long userId;
    private double realBalance;
    private double virtualBalance;
    public wallets(Long id, Long userId, double realBalance, double virtualBalance) {
        this.id = id;
        this.userId = userId;
        this.realBalance = realBalance;
        this.virtualBalance = virtualBalance;
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
   public void setUserId(Long userId) {}
    public double getRealBalance() {
        return realBalance;
    }
    public void setRealBalance(double realBalance) {
    }

    public double getVirtualBalance() {
        return virtualBalance;
    }
    public void setVirtualBalance(double virtualBalance) {}

}
