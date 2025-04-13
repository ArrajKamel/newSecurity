package com.kamel.backend.controller;

import com.kamel.backend.model.MyUser;
import com.kamel.backend.serivce.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService _userService;
    @Autowired
    public UserController(UserService userService) {
        _userService = userService;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam UUID userId) {
        _userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/disable")
    public ResponseEntity<?> disableUser(@RequestParam UUID userId) {
        _userService.disableUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/enable")
    public ResponseEntity<?> enableUser(@RequestParam UUID userId) {
        _userService.enableUser(userId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/all")
    public ResponseEntity<?> getUsers() {
        List<MyUser> users = _userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.FOUND);
    }


}
