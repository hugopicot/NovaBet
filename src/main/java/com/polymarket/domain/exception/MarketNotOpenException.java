package com.polymarket.domain.exception;

public class MarketNotOpenException extends DomainException {
    public MarketNotOpenException(String message) {
        super(message);
    }
}
