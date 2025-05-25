package com.kamel.backend.exception;

public class ConcurrencyRetryException extends RuntimeException {
    public ConcurrencyRetryException(String message) {
        super(message);
    }

    public ConcurrencyRetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
