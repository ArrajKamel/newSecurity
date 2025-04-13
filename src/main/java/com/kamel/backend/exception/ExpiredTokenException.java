package com.kamel.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(HttpStatus.GONE)
public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() {
        super("Verification token has expired");
    }

    public ExpiredTokenException(LocalDateTime expiryDate) {
        super("Token expired on " + expiryDate);
    }
}