package com.polymarket.domain.model;

import java.math.BigDecimal;

public record BetResult(
    Bet bet,
    BigDecimal sharePrice,
    int shareCount,
    BigDecimal totalCost,
    BigDecimal remainingBalance
) {
}
