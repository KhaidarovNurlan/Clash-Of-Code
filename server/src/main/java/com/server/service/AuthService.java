package com.server.service;

import com.server.model.User;
import com.server.repository.UserRepository;
import com.server.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private static final String ADMIN_EMAIL = "codemaster@example.com";

    public Map<String, Object> register(String username, String email, String password, String role) {
        if (userRepository.existsByEmailOrUsername(email, username)) {
            return Map.of("status", "error", "message", "Username or email already exists");
        }

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));

        if (email.equalsIgnoreCase(ADMIN_EMAIL)) {
            u.setRole("admin");
        } else {
            u.setRole(role == null ? "student" : role);
        }

        userRepository.save(u);
        String token = jwtUtil.generateToken(u.getId(), u.getRole(), u.getUsername());

        return Map.of("status", "success", "user", u, "token", token);
    }

    public Map<String, Object> login(String email, String password) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return Map.of("status", "error", "message", "Invalid credentials");

        User u = opt.get();

        if (!passwordEncoder.matches(password, u.getPassword()))
            return Map.of("status", "error", "message", "Invalid credentials");

        if (u.getEmail().equalsIgnoreCase(ADMIN_EMAIL)) {
            u.setRole("admin");
            userRepository.save(u);
        }

        String token = jwtUtil.generateToken(u.getId(), u.getRole(), u.getUsername());
        return Map.of("status", "success", "user", u, "token", token);
    }

    public Long getUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("No token");
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    public User verifyToken(String token) {
        try {
            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) return null;
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
