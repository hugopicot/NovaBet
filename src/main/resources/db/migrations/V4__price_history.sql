CREATE TABLE price_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    outcome_id BIGINT NOT NULL,
    odds DOUBLE NOT NULL,
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_outcome_time (event_id, outcome_id, recorded_at)
);
