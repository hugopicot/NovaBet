package com.polymarket.domain.exception;

public class OtoServiceException extends DomainException {
    public OtoServiceException(String message) {
        super(message);
    }

    public OtoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
