package com.polymarket.infrastructure.polymarket;

import com.polymarket.infrastructure.polymarket.model.PolymarketEvent;
import com.polymarket.infrastructure.polymarket.model.PolymarketMarket;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PolymarketGammaClient {

    private static final String BASE_URL = "https://gamma-api.polymarket.com";

    private final PolymarketHttpClient httpClient;

    public PolymarketGammaClient(PolymarketHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<PolymarketMarket> listOpenMarkets(int limit, int offset) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("closed", "false");
        params.put("active", "true");
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        return httpClient.getAndParseList(BASE_URL + "/markets", params, PolymarketMarket.class);
    }

    public List<PolymarketMarket> listClosedMarkets(int limit, int offset) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("closed", "true");
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        return httpClient.getAndParseList(BASE_URL + "/markets", params, PolymarketMarket.class);
    }

    public PolymarketMarket getMarket(String marketId) {
        Map<String, String> params = Map.of();
        return httpClient.getAndParse(BASE_URL + "/markets/" + marketId, params, PolymarketMarket.class);
    }

    public List<PolymarketEvent> listEvents(int limit, int offset) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("closed", "false");
        params.put("active", "true");
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        return httpClient.getAndParseList(BASE_URL + "/events", params, PolymarketEvent.class);
    }

    public PolymarketEvent getEvent(String eventId) {
        Map<String, String> params = Map.of();
        return httpClient.getAndParse(BASE_URL + "/events/" + eventId, params, PolymarketEvent.class);
    }
}
