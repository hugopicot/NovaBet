package com.polymarket.model;

public class outcomes {
    private Long id;
    private Long eventId;
    private String label;
    private double odds;

    public  outcomes(Long id, Long eventId, String label, double odds) {
        this.id = id;
        this.eventId = eventId;
        this.label = label;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {}
    public Long getEventId() {
        return eventId;
    }
    public void setEventId(Long eventId) {}
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {}
    public double getOdds() {
        return odds;
    }
    public void setOdds(double odds) {}

}
