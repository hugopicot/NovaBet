package com.polymarket.infrastructure.polymarket.model;

import com.google.gson.annotations.SerializedName;

public class PolymarketPrice {

    @SerializedName("token_id")
    private String tokenId;

    private Double price;

    private String side;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}
