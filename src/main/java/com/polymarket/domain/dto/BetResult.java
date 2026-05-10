package com.polymarket.domain.dto;

import com.polymarket.model.bets;

import java.math.BigDecimal;

public record BetResult(
    bets bet,
    BigDecimal sharePrice,
    int shareCount,
    BigDecimal totalCost,
    BigDecimal remainingBalance
) {}
