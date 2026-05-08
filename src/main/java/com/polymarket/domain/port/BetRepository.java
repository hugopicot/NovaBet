package com.polymarket.domain.port;

import com.polymarket.domain.model.Bet;

import java.util.List;

public interface BetRepository {
    Bet save(Bet bet);

    List<Bet> findByUserId(long userId);
}
