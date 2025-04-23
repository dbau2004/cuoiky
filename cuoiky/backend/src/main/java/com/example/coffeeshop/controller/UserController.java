package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.RoleRequestDTO;
import com.example.coffeeshop.dto.UserResponseDTO;
import com.example.coffeeshop.entity.User;
import com.example.coffeeshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            UserResponseDTO response = new UserResponseDTO(
                registeredUser.getId(),
                registeredUser.getUsername(),
                registeredUser.getEmail(),
                registeredUser.getPhone(),
                registeredUser.getRole(),
                "Registration successful"
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            UserResponseDTO errorResponse = new UserResponseDTO(null, null, null, null, null, e.getMessage());
            if (e.getMessage().equals("Username already exists")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("UserController: Processing login for username: " + user.getUsername());
        try {
            String token = userService.loginUser(user.getUsername(), user.getPassword());
            System.out.println("UserController: Login successful, token: " + token);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            System.out.println("UserController: Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            UserResponseDTO response = new UserResponseDTO(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail(),
                createdUser.getPhone(),
                createdUser.getRole(),
                "User created successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            UserResponseDTO errorResponse = new UserResponseDTO(null, null, null, null, null, e.getMessage());
            if (e.getMessage().equals("Username already exists")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            UserResponseDTO response = new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole(),
                "User updated successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            UserResponseDTO errorResponse = new UserResponseDTO(null, null, null, null, null, e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> assignRole(@PathVariable Long id, @RequestBody RoleRequestDTO roleRequest) {
        try {
            User updatedUser = userService.assignRole(id, roleRequest.getRole());
            UserResponseDTO response = new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole(),
                "Role assigned successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            UserResponseDTO errorResponse = new UserResponseDTO(null, null, null, null, null, e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            UserResponseDTO response = new UserResponseDTO(null, null, null, null, null, "User deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            UserResponseDTO errorResponse = new UserResponseDTO(null, null, null, null, null, e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<UserResponseDTO> checkUsername(@PathVariable String username) {
        boolean exists = userService.checkUsernameExists(username);
        UserResponseDTO response = new UserResponseDTO(null, null, null, null, null,
            exists ? "Username is already taken" : "Username is available");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}