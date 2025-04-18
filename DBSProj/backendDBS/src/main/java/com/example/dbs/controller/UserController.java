package com.example.dbs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dbs.model.User;
import com.example.dbs.service.UserService;

@RestController
@RequestMapping("/api/user") // Base endpoint path
public class UserController {
    private final UserService userService;

    // Dependency injection
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> userOptional = userService.getUserByEmail(email);
        // If user found return 200 OK, else return 404 NOT FOUND
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            // return 409 CONFLICT if user exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User createdUser = userService.saveUser(user);
        // return 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    //TODO: Special handling necessary to update special user fields (RegNo etc.)
    @PutMapping("/{email}")
    public ResponseEntity<User> updateUser(@PathVariable String email, @RequestBody User userDetails) {
        Optional<User> existingUserOptional = userService.getUserByEmail(email);
        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOptional.get();
        existingUser.setName(userDetails.getName());
        existingUser.setPhone(userDetails.getPhone());
        // Considering email is primary key and intrinsic to account: Cannot change

        User updatedUser = userService.saveUser(existingUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        if (!userService.existsByEmail(email)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(email);
        // return 204 NO CONTENT
        return ResponseEntity.noContent().build();
    }
}