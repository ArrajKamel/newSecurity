package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenCreationException extends RuntimeException {
  public TokenCreationException() {
    super("cannot create verification token: ");
  }

  public TokenCreationException( String message ) {
    super(message);
  }
}