package com.polymarket.domain.service;

import com.polymarket.domain.dto.BetRequest;
import com.polymarket.domain.dto.BetResult;
import com.polymarket.domain.dto.OutcomeLabel;

import java.math.BigDecimal;

public interface BettingService {
    BetResult buyShares(BetRequest request);

    BigDecimal calculateSharePrice(double yesProbability, OutcomeLabel outcome);

    int calculateShareCount(BigDecimal amount, BigDecimal sharePrice);

    BigDecimal calculatePotentialWin(int shareCount);
}
