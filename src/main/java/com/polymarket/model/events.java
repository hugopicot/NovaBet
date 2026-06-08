package com.polymarket.model;

public class events {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String resolution;
    private String createdAt;
    private String polymarketId;
    private String polymarketConditionId;
    private String source;
    private String endDate;
    private String imageUrl;

    public events(Long id, String title, String description, String status, String resolution, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.resolution = resolution;
        this.createdAt = createdAt;
    }

    public events(Long id, String title, String description, String status, String resolution, String createdAt,
                  String polymarketId, String polymarketConditionId, String source, String endDate, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.resolution = resolution;
        this.createdAt = createdAt;
        this.polymarketId = polymarketId;
        this.polymarketConditionId = polymarketConditionId;
        this.source = source;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
    }

    public events(String title, String description, String status, String resolution, String createdAt) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.resolution = resolution;
        this.createdAt = createdAt;
    }

    public events() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPolymarketId() {
        return polymarketId;
    }

    public void setPolymarketId(String polymarketId) {
        this.polymarketId = polymarketId;
    }

    public String getPolymarketConditionId() {
        return polymarketConditionId;
    }

    public void setPolymarketConditionId(String polymarketConditionId) {
        this.polymarketConditionId = polymarketConditionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "events{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
