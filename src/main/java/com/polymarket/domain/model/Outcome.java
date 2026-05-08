package com.polymarket.domain.model;

import java.math.BigDecimal;

public record Outcome(
    long id,
    long eventId,
    OutcomeLabel label,
    BigDecimal odds
) {
    public Outcome {
        if (label == null) {
            throw new IllegalArgumentException("Label cannot be null");
        }
        if (odds == null || odds.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Odds must be greater than zero");
        }
    }

    public BigDecimal impliedProbability() {
        return BigDecimal.ONE.divide(odds, 10, java.math.RoundingMode.HALF_UP);
    }
}
