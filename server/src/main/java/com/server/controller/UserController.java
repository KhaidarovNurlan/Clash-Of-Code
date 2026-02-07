package com.server.controller;

import com.server.service.AuthService;
import com.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        return userService.getUserProfile(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        String username = (String) body.get("username");
        return ResponseEntity.ok(userService.updateProfile(userId, username));
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<?> getCourseProgress(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        Map<String, Object> progress = userService.getCourseProgress(userId, courseId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/submissions")
    public ResponseEntity<?> getSubmissions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        Map<String, Object> data = userService.getUserSubmissions(userId, limit, offset);
        return ResponseEntity.ok(data);
    }
}
