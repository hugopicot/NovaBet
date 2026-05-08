package com.polymarket.domain.port;

import com.polymarket.domain.model.Outcome;
import com.polymarket.domain.model.OutcomeLabel;

import java.util.Optional;

public interface OutcomeRepository {
    Optional<Outcome> findById(long id);

    Optional<Outcome> findByEventIdAndLabel(long eventId, OutcomeLabel label);
}
