package com.polymarket.domain.exception;

public class WageringServiceException extends DomainException {
    public WageringServiceException(String message) {
        super(message);
    }

    public WageringServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
