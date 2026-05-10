package com.polymarket.domain.service;

import com.polymarket.model.events;

import java.util.List;

public interface MarketService {

    List<events> getAllMarkets();

    events getMarketById(Long id);

    events createMarket(events event);

    events updateMarket(events event);

    void deleteMarket(Long id);
}