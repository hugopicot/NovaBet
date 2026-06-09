package com.polymarket.infrastructure.polymarket;

import com.polymarket.infrastructure.polymarket.model.PolymarketPrice;
import com.polymarket.infrastructure.polymarket.model.PolymarketPriceHistoryPoint;
import com.polymarket.infrastructure.polymarket.model.PolymarketPriceHistoryResponse;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PolymarketClobClient {

    private static final Logger LOGGER = Logger.getLogger(PolymarketClobClient.class.getName());
    private static final String BASE_URL = "https://clob.polymarket.com";

    private final PolymarketHttpClient httpClient;

    public PolymarketClobClient(PolymarketHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public PolymarketPrice getPrice(String tokenId, String side) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("token_id", tokenId);
        params.put("side", side);
        return httpClient.getAndParse(BASE_URL + "/price", params, PolymarketPrice.class);
    }

    public List<PolymarketPrice> getPrices(List<String> tokenIds, List<String> sides) {
        Map<String, List<String>> params = new java.util.LinkedHashMap<>();
        params.put("token_ids", tokenIds);
        params.put("sides", sides);
        return httpClient.getAndParseListMultiValueParams(BASE_URL + "/prices", params, PolymarketPrice.class);
    }

    public PolymarketPriceHistoryResponse getPriceHistory(String tokenId, String interval, Long startTs, Long endTs) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("market", tokenId);
        params.put("interval", interval);
        if (startTs != null) {
            params.put("startTs", String.valueOf(startTs));
        }
        if (endTs != null) {
            params.put("endTs", String.valueOf(endTs));
        }

        String rawJson = httpClient.get(BASE_URL + "/prices-history", params);
        System.out.println("[PolymarketClobClient] Price history raw response length for token " + tokenId + ": " + (rawJson != null ? rawJson.length() : 0));

        if (rawJson == null || rawJson.isBlank()) {
            System.out.println("[PolymarketClobClient] Price history returned empty/null for token: " + tokenId);
            return null;
        }

        rawJson = rawJson.trim();
        System.out.println("[PolymarketClobClient] Price history raw JSON (first 200 chars): " + rawJson.substring(0, Math.min(200, rawJson.length())));

        try {
            PolymarketPriceHistoryResponse response = httpClient.getGson().fromJson(rawJson, PolymarketPriceHistoryResponse.class);
            if (response == null || response.getHistory() == null || response.getHistory().isEmpty()) {
                System.out.println("[PolymarketClobClient] Price history parsed but empty for token: " + tokenId);
                return null;
            }
            System.out.println("[PolymarketClobClient] Price history parsed successfully: " + response.getHistory().size() + " points for token " + tokenId);
            return response;
        } catch (Exception e) {
            System.out.println("[PolymarketClobClient] Failed to parse price history for token " + tokenId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
