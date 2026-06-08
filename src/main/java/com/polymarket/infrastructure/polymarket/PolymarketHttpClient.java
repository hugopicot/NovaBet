package com.polymarket.infrastructure.polymarket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PolymarketHttpClient {

    private static final Logger LOGGER = Logger.getLogger(PolymarketHttpClient.class.getName());
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final HttpClient httpClient;
    private final Gson gson;

    public PolymarketHttpClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.gson = new Gson();
    }

    public String get(String baseUrl, Map<String, String> params) {
        String url = buildUrl(baseUrl, params);
        return executeWithRetry(url);
    }

    public String getMultiValueParams(String baseUrl, Map<String, List<String>> params) {
        String url = buildUrlMultiValue(baseUrl, params);
        return executeWithRetry(url);
    }

    public <T> T getAndParse(String baseUrl, Map<String, String> params, Class<T> type) {
        String json = get(baseUrl, params);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, type);
    }

    public <T> List<T> getAndParseList(String baseUrl, Map<String, String> params, Class<T> elementType) {
        String json = get(baseUrl, params);
        if (json == null) {
            return List.of();
        }
        var type = TypeToken.getParameterized(List.class, elementType).getType();
        return gson.fromJson(json, type);
    }

    public <T> List<T> getAndParseListMultiValueParams(String baseUrl, Map<String, List<String>> params, Class<T> elementType) {
        String json = getMultiValueParams(baseUrl, params);
        if (json == null) {
            return List.of();
        }
        var type = TypeToken.getParameterized(List.class, elementType).getType();
        return gson.fromJson(json, type);
    }

    private String executeWithRetry(String url) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(15))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return response.body();
                }

                if (response.statusCode() == 429) {
                    LOGGER.warning("Rate limited on attempt " + attempt + " for " + url);
                    Thread.sleep(RETRY_DELAY_MS * attempt * 2);
                    continue;
                }

                LOGGER.warning("HTTP " + response.statusCode() + " for " + url + ": " + response.body());
                return null;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } catch (Exception e) {
                LOGGER.warning("Request failed on attempt " + attempt + " for " + url + ": " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private String buildUrl(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("?");
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            first = false;
        }
        return sb.toString();
    }

    public Gson getGson() {
        return gson;
    }

    public String buildUrlMultiValue(String baseUrl, Map<String, List<String>> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("?");
        boolean first = true;
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            for (String value : entry.getValue()) {
                if (!first) {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                sb.append("=");
                sb.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                first = false;
            }
        }
        return sb.toString();
    }
}
