package com.server.controller;

import com.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")

public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String email = (String) body.get("email");
        String password = (String) body.get("password");
        String role = (String) body.get("role");
        return ResponseEntity.ok(authService.register(username, email, password, role));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        String password = (String) body.get("password");
        return ResponseEntity.ok(authService.login(email, password));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body(Map.of("error", "Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);
        var user = authService.verifyToken(token);
        if (user == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid token"));
        }
        return ResponseEntity.ok(user);
    }
}
