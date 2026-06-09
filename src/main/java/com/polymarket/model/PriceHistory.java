package com.polymarket.model;

public class PriceHistory {
    private Long id;
    private Long eventId;
    private Long outcomeId;
    private double odds;
    private String recordedAt;

    public PriceHistory() {}

    public PriceHistory(Long id, Long eventId, Long outcomeId, double odds, String recordedAt) {
        this.id = id;
        this.eventId = eventId;
        this.outcomeId = outcomeId;
        this.odds = odds;
        this.recordedAt = recordedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Long getOutcomeId() { return outcomeId; }
    public void setOutcomeId(Long outcomeId) { this.outcomeId = outcomeId; }
    public double getOdds() { return odds; }
    public void setOdds(double odds) { this.odds = odds; }
    public String getRecordedAt() { return recordedAt; }
    public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }
}
