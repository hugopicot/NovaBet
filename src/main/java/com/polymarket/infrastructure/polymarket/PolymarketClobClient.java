package com.polymarket.infrastructure.polymarket;

import com.polymarket.infrastructure.polymarket.model.PolymarketPrice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PolymarketClobClient {

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
}
