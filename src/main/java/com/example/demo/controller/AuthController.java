package com.example.demo.controller;

import com.example.demo.dto.SignupRequest;
import com.example.demo.exception.EmailExistsException;
import com.example.demo.model.MyUser;
import com.example.demo.serivce.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid SignupRequest signupRequest) {
        try {
            _authService.createUser(signupRequest);
            return ResponseEntity.ok().build();
        }catch (EmailExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        List<MyUser> users = _authService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.FOUND);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequest request) {
        _authService.handleSignup(request);
        return "success";
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

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam UUID userId) {
        _authService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }


}
