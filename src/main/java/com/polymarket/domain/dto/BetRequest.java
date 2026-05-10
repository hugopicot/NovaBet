package com.polymarket.domain.dto;

import java.math.BigDecimal;

public record BetRequest(
    long userId,
    long eventId,
    OutcomeLabel outcome,
    BigDecimal amount,
    boolean useVirtualBalance
) {}
