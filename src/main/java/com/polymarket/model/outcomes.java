package com.polymarket.model;

public class outcomes {
    private Long id;
    private Long eventId;
    private String label;
    private double odds;
    private String polymarketTokenId;

    public outcomes(Long id, Long eventId, String label, double odds) {
        this.id = id;
        this.eventId = eventId;
        this.label = label;
        this.odds = odds;
    }

    public outcomes(Long id, Long eventId, String label, double odds, String polymarketTokenId) {
        this.id = id;
        this.eventId = eventId;
        this.label = label;
        this.odds = odds;
        this.polymarketTokenId = polymarketTokenId;
    }

    public outcomes(Long eventId, String label, double odds) {
        this.eventId = eventId;
        this.label = label;
        this.odds = odds;
    }

    public outcomes() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }

    public String getPolymarketTokenId() {
        return polymarketTokenId;
    }

    public void setPolymarketTokenId(String polymarketTokenId) {
        this.polymarketTokenId = polymarketTokenId;
    }

    @Override
    public String toString() {
        return "outcomes{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", label='" + label + '\'' +
                ", odds=" + odds +
                '}';
    }
}
