package com.polymarket.domain.model;

import java.time.Instant;

public record Event(
    long id,
    String title,
    String description,
    EventStatus status,
    String resolution,
    Instant createdAt
) {
    public Event {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }

    public boolean isOpen() {
        return status == EventStatus.OPEN;
    }
}
