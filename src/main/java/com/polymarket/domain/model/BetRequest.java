package com.polymarket.domain.model;

import java.math.BigDecimal;

public record BetRequest(
    long userId,
    long eventId,
    OutcomeLabel outcome,
    BigDecimal amount,
    boolean useVirtualBalance
) {
    public BetRequest {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be greater than zero");
        }
    }
}
