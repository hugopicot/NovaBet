package com.polymarket.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
    long id,
    long userId,
    TransactionType type,
    BigDecimal amount,
    Instant createdAt
) {
    public Transaction {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
    }
}
