package com.server.controller;

import com.server.model.User;
import com.server.repository.CourseRepository;
import com.server.repository.TournamentRepository;
import com.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AuthService authService;
    @Autowired private CourseRepository courseRepository;
    @Autowired private TournamentRepository tournamentRepository;

    private boolean isAdmin(User user) {
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    @DeleteMapping("/course/{id}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            User user = authService.verifyToken(authHeader.replace("Bearer ", ""));
            if (!isAdmin(user)) {
                return ResponseEntity.status(403).body(
                        java.util.Map.of("status", "error", "message", "Access denied")
                );
            }

            if (!courseRepository.existsById(id)) {
                return ResponseEntity.status(404).body(
                        java.util.Map.of("status", "error", "message", "Course not found")
                );
            }

            courseRepository.deleteById(id);
            return ResponseEntity.ok(java.util.Map.of("status", "success", "message", "Course deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("status", "error", "message", e.getMessage())
            );
        }
    }

    @DeleteMapping("/tournament/{id}")
    public ResponseEntity<?> deleteTournament(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            User user = authService.verifyToken(authHeader.replace("Bearer ", ""));
            if (!isAdmin(user)) {
                return ResponseEntity.status(403).body(
                        java.util.Map.of("status", "error", "message", "Access denied")
                );
            }

            if (!tournamentRepository.existsById(id)) {
                return ResponseEntity.status(404).body(
                        java.util.Map.of("status", "error", "message", "Tournament not found")
                );
            }

            tournamentRepository.deleteById(id);
            return ResponseEntity.ok(java.util.Map.of("status", "success", "message", "Tournament deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("status", "error", "message", e.getMessage())
            );
        }
    }
}
