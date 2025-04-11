package com.example.demo.controller;


import com.example.demo.exception.TokenCreationException;
import com.example.demo.model.EmailVerificationToken;
import com.example.demo.model.MyUser;
import com.example.demo.serivce.AuthService;
import com.example.demo.serivce.EmailVerificationTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/token")
public class EmailVerificationTokenController {

    private final EmailVerificationTokenService _emailVerificationTokenService;
    private final AuthService _authService;

    @Autowired
    public EmailVerificationTokenController(EmailVerificationTokenService emailVerificationTokenService, AuthService authService) {
        this._emailVerificationTokenService = emailVerificationTokenService;
        this._authService = authService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam UUID userId) {
        MyUser user = _authService.getUserById(userId);
        if(user == null) {
            throw new TokenCreationException("User not found");
        }
        EmailVerificationToken token = _emailVerificationTokenService.createToken(user);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam UUID userId) {
        try {
            _emailVerificationTokenService.deleteUserToken(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTokens() {
        return new ResponseEntity<>(_emailVerificationTokenService.getAllTokens(), HttpStatus.OK);
    }

}
