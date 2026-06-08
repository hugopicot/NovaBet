package com.polymarket.domain.service;

import com.polymarket.dao.eventsDao;
import com.polymarket.dao.outcomesDao;
import com.polymarket.infrastructure.polymarket.PolymarketClobClient;
import com.polymarket.infrastructure.polymarket.PolymarketGammaClient;
import com.polymarket.infrastructure.polymarket.model.PolymarketMarket;
import com.polymarket.infrastructure.polymarket.model.PolymarketPrice;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PolymarketSyncService {

    private static final Logger LOGGER = Logger.getLogger(PolymarketSyncService.class.getName());

    private static final long MARKET_SYNC_INTERVAL_MIN = 5;
    private static final long PRICE_SYNC_INTERVAL_MIN = 1;
    private static final long RESOLUTION_SYNC_INTERVAL_MIN = 2;
    private static final int MARKET_BATCH_SIZE = 20;
    private static final int MAX_POLYMARKET_MARKETS = 50;

    private final PolymarketGammaClient gammaClient;
    private final PolymarketClobClient clobClient;
    private final eventsDao eventDao;
    private final outcomesDao outcomeDao;
    private final PolymarketResolutionService resolutionService;

    private ScheduledExecutorService scheduler;

    public PolymarketSyncService(
            PolymarketGammaClient gammaClient,
            PolymarketClobClient clobClient,
            eventsDao eventDao,
            outcomesDao outcomeDao,
            PolymarketResolutionService resolutionService
    ) {
        this.gammaClient = gammaClient;
        this.clobClient = clobClient;
        this.eventDao = eventDao;
        this.outcomeDao = outcomeDao;
        this.resolutionService = resolutionService;
    }

    public void start() {
        scheduler = Executors.newScheduledThreadPool(3, r -> {
            Thread t = new Thread(r, "polymarket-sync");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleWithFixedDelay(
                this::syncMarkets,
                0,
                MARKET_SYNC_INTERVAL_MIN,
                TimeUnit.MINUTES
        );

        scheduler.scheduleWithFixedDelay(
                this::syncPrices,
                10,
                PRICE_SYNC_INTERVAL_MIN,
                TimeUnit.MINUTES
        );

        scheduler.scheduleWithFixedDelay(
                this::syncResolutions,
                30,
                RESOLUTION_SYNC_INTERVAL_MIN,
                TimeUnit.MINUTES
        );

        LOGGER.info("PolymarketSyncService started");
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("PolymarketSyncService stopped");
    }

    public void syncMarkets() {
        try {
            long existingCount = eventDao.findOpenBySource("POLYMARKET").size();
            if (existingCount >= MAX_POLYMARKET_MARKETS) {
                LOGGER.info("Market sync skipped: already at max capacity (" + existingCount + "/" + MAX_POLYMARKET_MARKETS + ")");
                return;
            }

            int offset = 0;
            int totalSynced = 0;
            int remaining = MAX_POLYMARKET_MARKETS - (int) existingCount;

            while (remaining > 0) {
                int batchSize = Math.min(MARKET_BATCH_SIZE, remaining);
                List<PolymarketMarket> markets = gammaClient.listOpenMarkets(batchSize, offset);
                if (markets == null || markets.isEmpty()) {
                    break;
                }

                for (PolymarketMarket market : markets) {
                    if (market.getId() == null || market.getEnableOrderBook() == null || !market.getEnableOrderBook()) {
                        continue;
                    }
                    if (eventDao.findByPolymarketId(market.getId()) != null) {
                        continue;
                    }
                    upsertMarket(market);
                    totalSynced++;
                    remaining--;
                    if (remaining <= 0) break;
                }

                if (markets.size() < batchSize) {
                    break;
                }
                offset += batchSize;
            }

            LOGGER.info("Market sync completed: " + totalSynced + " new markets imported");
        } catch (Exception e) {
            LOGGER.warning("Market sync failed: " + e.getMessage());
        }
    }

    public void syncPrices() {
        try {
            List<events> polymarketEvents = eventDao.findOpenBySource("POLYMARKET");
            int updated = 0;

            for (events event : polymarketEvents) {
                List<outcomes> eventOutcomes = outcomeDao.findByEventId(event.getId());
                List<String> tokenIds = new ArrayList<>();

                for (outcomes o : eventOutcomes) {
                    if (o.getPolymarketTokenId() != null && !o.getPolymarketTokenId().isEmpty()) {
                        tokenIds.add(o.getPolymarketTokenId());
                    }
                }

                if (tokenIds.isEmpty()) {
                    continue;
                }

                try {
                    List<String> sides = new ArrayList<>();
                    for (int i = 0; i < tokenIds.size(); i++) {
                        sides.add("BUY");
                    }
                    List<PolymarketPrice> prices = clobClient.getPrices(tokenIds, sides);
                    if (prices != null) {
                        for (PolymarketPrice price : prices) {
                            if (price.getTokenId() != null && price.getPrice() != null) {
                                outcomes o = outcomeDao.findByPolymarketTokenId(price.getTokenId());
                                if (o != null) {
                                    double newOdds = Math.max(0.01, Math.min(0.99, price.getPrice()));
                                    outcomeDao.updateOdds(o.getId(), newOdds);
                                    updated++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warning("Price sync failed for event " + event.getId() + ": " + e.getMessage());
                }
            }

            LOGGER.info("Price sync completed: " + updated + " outcomes updated across " + polymarketEvents.size() + " events");
        } catch (Exception e) {
            LOGGER.warning("Price sync failed: " + e.getMessage());
        }
    }

    public void syncResolutions() {
        try {
            int offset = 0;
            int resolved = 0;

            while (true) {
                List<PolymarketMarket> closedMarkets = gammaClient.listClosedMarkets(MARKET_BATCH_SIZE, offset);
                if (closedMarkets == null || closedMarkets.isEmpty()) {
                    break;
                }

                for (PolymarketMarket market : closedMarkets) {
                    if (market.getId() == null) {
                        continue;
                    }

                    events existing = eventDao.findByPolymarketId(market.getId());
                    if (existing == null || !"OPEN".equals(existing.getStatus())) {
                        continue;
                    }

                    String winningLabel = determineWinner(market);
                    if (winningLabel != null) {
                        resolutionService.settleMarket(existing.getId(), winningLabel);
                        resolved++;
                    }
                }

                if (closedMarkets.size() < MARKET_BATCH_SIZE) {
                    break;
                }
                offset += MARKET_BATCH_SIZE;
            }

            LOGGER.info("Resolution sync completed: " + resolved + " markets resolved");
        } catch (Exception e) {
            LOGGER.warning("Resolution sync failed: " + e.getMessage());
        }
    }

    private void upsertMarket(PolymarketMarket market) {
        events existing = eventDao.findByPolymarketId(market.getId());

        if (existing == null) {
            events event = new events();
            event.setTitle(market.getQuestion());
            event.setDescription(market.getDescription());
            event.setStatus("OPEN");
            event.setResolution(null);
            event.setCreatedAt(nowFormatted());
            event.setPolymarketId(market.getId());
            event.setPolymarketConditionId(market.getConditionId());
            event.setSource("POLYMARKET");
            event.setEndDate(convertToMysqlDatetime(market.getEndDate()));
            event.setImageUrl(market.getImage());

            eventDao.add(event);

            if (event.getId() == null) {
                LOGGER.warning("Failed to insert event, skipping outcomes: " + market.getQuestion());
                return;
            }

            String[] labels = parseJsonArray(market.getOutcomes());
            String[] prices = parseJsonArray(market.getOutcomePrices());
            String[] tokenIds = parseJsonArray(market.getClobTokenIds());

            for (int i = 0; i < labels.length; i++) {
                outcomes outcome = new outcomes();
                outcome.setEventId(event.getId());
                outcome.setLabel(labels[i].trim());

                double odds = 0.5;
                if (i < prices.length) {
                    try {
                        odds = Double.parseDouble(prices[i].trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
                outcome.setOdds(odds);

                if (i < tokenIds.length) {
                    outcome.setPolymarketTokenId(tokenIds[i].trim());
                }

                outcomeDao.add(outcome);
            }

        } else {
            String[] prices = parseJsonArray(market.getOutcomePrices());
            List<outcomes> existingOutcomes = outcomeDao.findByEventId(existing.getId());

            for (int i = 0; i < existingOutcomes.size() && i < prices.length; i++) {
                try {
                    double odds = Double.parseDouble(prices[i].trim());
                    outcomeDao.updateOdds(existingOutcomes.get(i).getId(), odds);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private String determineWinner(PolymarketMarket market) {
        String[] prices = parseJsonArray(market.getOutcomePrices());
        String[] labels = parseJsonArray(market.getOutcomes());

        if (prices.length < 2 || labels.length < 2) {
            return null;
        }

        try {
            double yesPrice = Double.parseDouble(prices[0].trim());
            double noPrice = Double.parseDouble(prices[1].trim());

            if (yesPrice >= 0.99) {
                return labels[0].trim();
            }
            if (noPrice >= 0.99) {
                return labels[1].trim();
            }
        } catch (NumberFormatException ignored) {
        }

        return null;
    }

    private String[] parseJsonArray(String json) {
        if (json == null || json.isEmpty()) {
            return new String[0];
        }
        String cleaned = json.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return Arrays.stream(cleaned.split(","))
                .map(s -> s.replace("\"", "").trim())
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    private String nowFormatted() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String convertToMysqlDatetime(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return null;
        }
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(isoDate, DateTimeFormatter.ISO_DATE_TIME);
            return zdt.withZoneSameInstant(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(isoDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                LOGGER.warning("Could not parse date: " + isoDate);
                return null;
            }
        }
    }
}
