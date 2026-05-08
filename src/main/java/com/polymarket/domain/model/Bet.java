package com.polymarket.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Bet(
    long id,
    long userId,
    long outcomeId,
    BigDecimal amount,
    BigDecimal potentialWin,
    BigDecimal sharePrice,
    int shareCount,
    BetStatus status,
    Instant createdAt
) {
    public Bet {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be greater than zero");
        }
        if (sharePrice == null || sharePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Share price must be greater than zero");
        }
        if (shareCount <= 0) {
            throw new IllegalArgumentException("Share count must be greater than zero");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
