package com.kamel.backend.controller;

import com.kamel.backend.model.EmailVerificationToken;
import com.kamel.backend.serivce.EmailVerificationTokenService;
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


    @Autowired
    public EmailVerificationTokenController(EmailVerificationTokenService emailVerificationTokenService) {
        this._emailVerificationTokenService = emailVerificationTokenService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam UUID userId) {
        EmailVerificationToken token = _emailVerificationTokenService.createToken(userId);
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllTokens() {
        return new ResponseEntity<>(_emailVerificationTokenService.getAllTokens(), HttpStatus.OK);
    }

}
