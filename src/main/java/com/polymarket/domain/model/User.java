package com.polymarket.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record User(
    long id,
    String username,
    String email,
    String passwordHash,
    Instant createdAt
) {
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank");
        }
    }
}
