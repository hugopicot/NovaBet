package com.polymarket.domain.model;

import java.math.BigDecimal;

public record Wallet(
    long id,
    long userId,
    BigDecimal realBalance,
    BigDecimal virtualBalance
) {
    public Wallet {
        if (realBalance == null) realBalance = BigDecimal.ZERO;
        if (virtualBalance == null) virtualBalance = BigDecimal.ZERO;
        if (realBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Real balance cannot be negative");
        }
        if (virtualBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Virtual balance cannot be negative");
        }
    }

    public BigDecimal totalBalance() {
        return realBalance.add(virtualBalance);
    }
}
