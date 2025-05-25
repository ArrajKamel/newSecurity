package com.kamel.backend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
public class CartIsEmptyException extends RuntimeException {
    public CartIsEmptyException() {
        super("cart is empty");
    }
}
