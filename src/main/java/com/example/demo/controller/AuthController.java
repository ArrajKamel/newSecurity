package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.exception.EmailExistsException;
import com.example.demo.model.MyUser;
import com.example.demo.serivce.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService _authService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        _authService = authService;
        this.authenticationManager = authenticationManager;
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

    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequest request) {
        _authService.handleSignup(request);
        return "success";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // handle updating the lastLoginAt attribute
            MyUser user = (MyUser) authentication.getPrincipal();
            _authService.setLoginDate(user);

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
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
