package com.polymarket.infrastructure.polymarket.model;

public class PolymarketPriceHistoryPoint {

    private long t;
    private String p;

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }
}
