package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // return HTTP 409
public class EmailExistsException extends  RuntimeException{
    public EmailExistsException(String email) {
        super(email + " already exists");
    }
}
