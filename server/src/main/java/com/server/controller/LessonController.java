package com.server.controller;

import com.server.service.AuthService;
import com.server.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lessons")

public class LessonController {

    @Autowired private LessonService lessonService;
    @Autowired private AuthService authService;

    @PostMapping("/{lessonId}/complete")
    public ResponseEntity<?> completeLesson(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long lessonId,
            @RequestParam Long courseId) {
        Long userId = authService.getUserIdFromAuthHeader(authHeader);
        return ResponseEntity.ok(lessonService.completeLesson(userId, courseId, lessonId));
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<?> getLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.getLesson(lessonId));
    }
}
