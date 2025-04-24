package com.kamel.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // return HTTP 409
public class CartExistsException extends RuntimeException {
    public CartExistsException() {
        super("cart already exists");
    }
}
