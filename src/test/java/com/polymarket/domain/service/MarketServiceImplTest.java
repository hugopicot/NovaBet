package com.polymarket.domain.service;

import com.polymarket.dao.eventsDao;
import com.polymarket.dao.outcomesDao;
import com.polymarket.model.events;
import com.polymarket.model.outcomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MarketServiceImplTest {

    private eventsDao eventDao;
    private outcomesDao outcomeDao;
    private MarketServiceImpl marketService;

    @BeforeEach
    void setUp() throws SQLException {

        eventDao = mock(eventsDao.class);
        outcomeDao = mock(outcomesDao.class);

        marketService = new MarketServiceImpl(eventDao, outcomeDao);
    }

    @Test
    void testGetAllMarkets() {

        List<events> eventsList = new ArrayList<>();

        when(eventDao.getAll()).thenReturn(eventsList);

        List<events> result = marketService.getAllMarkets();

        assertNotNull(result);
    }

    @Test
    void testGetMarketById() {

        events event = new events();

        when(eventDao.findById(1L)).thenReturn(event);

        events result = marketService.getMarketById(1L);

        assertEquals(event, result);
    }

    @Test
    void testCreateMarket() {

        events event = new events();

        event.setStatus(null);
        event.setCreatedAt(null);

        events result = marketService.createMarket(event);

        assertEquals("OPEN", result.getStatus());
        assertNotNull(result.getCreatedAt());

        verify(eventDao).add(event);
    }

    @Test
    void testUpdateMarket() {

        events event = new events();

        events result = marketService.updateMarket(event);

        assertEquals(event, result);

        verify(eventDao).update(event);
    }

    @Test
    void testDeleteMarket() {

        List<outcomes> outcomesList = new ArrayList<>();

        outcomes outcome = new outcomes();
        outcome.setId(1L);

        outcomesList.add(outcome);

        when(outcomeDao.findByEventId(1L)).thenReturn(outcomesList);

        marketService.deleteMarket(1L);

        verify(outcomeDao).delete(1L);
        verify(eventDao).delete(1L);
    }
}
