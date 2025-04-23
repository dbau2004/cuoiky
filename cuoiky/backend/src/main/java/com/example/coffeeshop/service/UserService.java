package com.example.coffeeshop.service;

import com.example.coffeeshop.entity.User;
import com.example.coffeeshop.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final List<String> VALID_ROLES = List.of("ADMIN", "STAFF", "CUSTOMER");

    public User registerUser(User user) {
        System.out.println("Registering user: " + user.getUsername());

        // Validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new RuntimeException("Invalid email format");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Phone number is required");
        }
        if (user.getRole() == null || !VALID_ROLES.contains(user.getRole())) {
            user.setRole("CUSTOMER"); // Default role for registration
        }

        // Check for existing username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            System.out.println("Username already exists: " + user.getUsername());
            throw new RuntimeException("Username already exists");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("Encoded password: " + encodedPassword);
        user.setPassword(encodedPassword);

        // Save user
        User savedUser = userRepository.save(user);
        System.out.println("User saved: " + savedUser.getUsername());
        return savedUser;
    }

    public String loginUser(String username, String password) {
        System.out.println("Login attempt for username: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("User not found: " + username);
                    return new RuntimeException("Invalid credentials");
                });
        System.out.println("Stored password: " + user.getPassword());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Password mismatch for username: " + username + ", provided: " + password);
            throw new RuntimeException("Invalid credentials");
        }
        System.out.println("Creating JWT for username: " + username);
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole());
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
            System.out.println("JWT created: " + token);
            return token;
        } catch (Exception e) {
            System.out.println("Error creating JWT: " + e.getMessage());
            throw new RuntimeException("Failed to create JWT", e);
        }
    }

    public User createUser(User user) {
        // Validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new RuntimeException("Invalid email format");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Phone number is required");
        }
        if (user.getRole() == null || !VALID_ROLES.contains(user.getRole())) {
            throw new RuntimeException("Invalid role. Must be one of: " + VALID_ROLES);
        }

        // Check for existing username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User assignRole(Long id, String role) {
        System.out.println("Received role: " + role);
        if (role == null) {
            throw new RuntimeException("Role cannot be null");
        }
        role = role.trim();
        if (!VALID_ROLES.contains(role)) {
            throw new RuntimeException("Invalid role. Must be one of: " + VALID_ROLES);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}