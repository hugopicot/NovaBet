package com.polymarket.domain.service;

import com.polymarket.dao.eventsDao;
import com.polymarket.dao.outcomesDao;
import com.polymarket.model.events;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MarketServiceImpl implements MarketService {

    private final eventsDao eventDao;
    private final outcomesDao outcomeDao;

    public MarketServiceImpl(eventsDao eventDao, outcomesDao outcomeDao) throws SQLException {
        this.eventDao = eventDao;
        this.outcomeDao = outcomeDao;
    }

    @Override
    public List<events> getAllMarkets() {
        return eventDao.getAll();
    }

    @Override
    public events getMarketById(Long id) {
        return eventDao.findById(id);
    }

    @Override
    public events createMarket(events event) {
        if (event.getCreatedAt() == null || event.getCreatedAt().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            event.setCreatedAt(formatter.format(Instant.now()));
        }
        if (event.getStatus() == null || event.getStatus().isEmpty()) {
            event.setStatus("OPEN");
        }
        eventDao.add(event);
        return event;
    }

    @Override
    public events updateMarket(events event) {
        eventDao.update(event);
        return event;
    }

    @Override
    public void deleteMarket(Long id) {
        outcomeDao.findByEventId(id).forEach(outcome -> outcomeDao.delete(outcome.getId()));
        eventDao.delete(id);
    }
}