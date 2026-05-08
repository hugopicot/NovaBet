package com.polymarket.domain.service;

import com.polymarket.domain.model.BetRequest;
import com.polymarket.domain.model.BetResult;
import com.polymarket.domain.model.OutcomeLabel;

import java.math.BigDecimal;

public interface BettingService {
    BetResult buyShares(BetRequest request);

    BigDecimal calculateSharePrice(double yesProbability, OutcomeLabel outcome);

    int calculateShareCount(BigDecimal amount, BigDecimal sharePrice);

    BigDecimal calculatePotentialWin(int shareCount);
}
