package com.kamel.backend.controller;

import com.kamel.backend.dto.LoginRequest;
import com.kamel.backend.dto.SignupRequest;
import com.kamel.backend.exception.EmailExistsException;
import com.kamel.backend.model.MyUser;
import com.kamel.backend.serivce.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService _authService;

    @Autowired
    public AuthController(AuthService authService) {
        _authService = authService;
    }

//
//    @PostMapping("/create")
//    public ResponseEntity<?> create(@RequestBody @Valid SignupRequest signupRequest) {
//        try {
//            _authService.createUser(signupRequest);
//            return ResponseEntity.ok().build();
//        }catch (EmailExistsException ex){
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
//        }
//    }

    @GetMapping("/secure")
    public String secure() {
        return "secure";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequest request) {
        _authService.handleSignup(request);
        return "success";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return _authService.handleLogin(loginRequest);

    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam UUID tokenId) {
        try {
            _authService.confirmVerification(tokenId);
            return ResponseEntity.ok().build();
        }catch (EmailExistsException ex){
            return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
        }
    }

}
