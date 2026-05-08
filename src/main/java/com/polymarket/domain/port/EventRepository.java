package com.polymarket.domain.port;

import com.polymarket.domain.model.Event;
import com.polymarket.domain.model.EventStatus;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<Event> findById(long id);

    List<Event> findByStatus(EventStatus status);
}
