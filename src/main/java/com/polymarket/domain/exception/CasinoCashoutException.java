package com.polymarket.domain.exception;

public class CasinoCashoutException extends DomainException {
    public CasinoCashoutException(String message) {
        super(message);
    }

    public CasinoCashoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
