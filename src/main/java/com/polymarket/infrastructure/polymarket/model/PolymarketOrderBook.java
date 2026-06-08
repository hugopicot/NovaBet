package com.polymarket.infrastructure.polymarket.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PolymarketOrderBook {

    @SerializedName("asset_id")
    private String assetId;

    private String market;

    private List<PriceLevel> bids;

    private List<PriceLevel> asks;

    @SerializedName("hash")
    private String hash;

    @SerializedName("timestamp")
    private String timestamp;

    public static class PriceLevel {
        private String price;
        private String size;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public List<PriceLevel> getBids() {
        return bids;
    }

    public void setBids(List<PriceLevel> bids) {
        this.bids = bids;
    }

    public List<PriceLevel> getAsks() {
        return asks;
    }

    public void setAsks(List<PriceLevel> asks) {
        this.asks = asks;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
