package com.polymarket.infrastructure.polymarket.model;

import java.util.List;

public class PolymarketPriceHistoryResponse {

    private List<PolymarketPriceHistoryPoint> history;

    public List<PolymarketPriceHistoryPoint> getHistory() {
        return history;
    }

    public void setHistory(List<PolymarketPriceHistoryPoint> history) {
        this.history = history;
    }
}
