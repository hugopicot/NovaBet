package com.polymarket.infrastructure.polymarket.model;

import com.google.gson.annotations.SerializedName;

public class PolymarketMarket {

    private String id;

    @SerializedName("conditionId")
    private String conditionId;

    private String question;
    private String slug;
    private String description;
    private String outcomes;
    private String outcomePrices;

    @SerializedName("clobTokenIds")
    private String clobTokenIds;

    private Boolean active;
    private Boolean closed;
    private String volume;

    @SerializedName("volumeNum")
    private Double volumeNum;

    @SerializedName("liquidityNum")
    private Double liquidityNum;

    @SerializedName("endDate")
    private String endDate;

    private String image;

    @SerializedName("enableOrderBook")
    private Boolean enableOrderBook;

    private String category;

    @SerializedName("umaResolutionStatus")
    private String umaResolutionStatus;

    @SerializedName("automaticallyResolved")
    private Boolean automaticallyResolved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(String outcomes) {
        this.outcomes = outcomes;
    }

    public String getOutcomePrices() {
        return outcomePrices;
    }

    public void setOutcomePrices(String outcomePrices) {
        this.outcomePrices = outcomePrices;
    }

    public String getClobTokenIds() {
        return clobTokenIds;
    }

    public void setClobTokenIds(String clobTokenIds) {
        this.clobTokenIds = clobTokenIds;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Double getVolumeNum() {
        return volumeNum;
    }

    public void setVolumeNum(Double volumeNum) {
        this.volumeNum = volumeNum;
    }

    public Double getLiquidityNum() {
        return liquidityNum;
    }

    public void setLiquidityNum(Double liquidityNum) {
        this.liquidityNum = liquidityNum;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getEnableOrderBook() {
        return enableOrderBook;
    }

    public void setEnableOrderBook(Boolean enableOrderBook) {
        this.enableOrderBook = enableOrderBook;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUmaResolutionStatus() {
        return umaResolutionStatus;
    }

    public void setUmaResolutionStatus(String umaResolutionStatus) {
        this.umaResolutionStatus = umaResolutionStatus;
    }

    public Boolean getAutomaticallyResolved() {
        return automaticallyResolved;
    }

    public void setAutomaticallyResolved(Boolean automaticallyResolved) {
        this.automaticallyResolved = automaticallyResolved;
    }
}
