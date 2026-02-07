package com.server.controller;

import com.server.model.Lesson;
import com.server.model.UserCourse;
import com.server.model.UserLesson;
import com.server.repository.LessonRepository;
import com.server.repository.UserRepository;
import com.server.repository.UserCourseRepository;
import com.server.repository.UserLessonRepository;
import com.server.service.AuthService;
import com.server.service.CourseService;
import com.server.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courses")

public class CourseController {

    @Autowired private CourseService courseService;
    @Autowired private AuthService authService;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final UserLessonRepository userLessonRepository;
    private final JwtUtil jwtUtil;

    public CourseController(
            CourseService courseService,
            AuthService authService,
            LessonRepository lessonRepository,
            UserRepository userRepository,
            UserCourseRepository userCourseRepository,
            UserLessonRepository userLessonRepository,
            JwtUtil jwtUtil) {
        this.courseService = courseService;
        this.authService = authService;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.userCourseRepository = userCourseRepository;
        this.userLessonRepository = userLessonRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<?> getCourses(
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(Map.of(
                "total", courseService.countCourses(level),
                "courses", courseService.getCourses(level, limit, offset)
        ));
    }

    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        Long teacherId = authService.getUserIdFromAuthHeader(authHeader);
        return ResponseEntity.ok(courseService.createCourse(teacherId, body));
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/complete")
    public ResponseEntity<?> completeLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            Optional<Lesson> lessonOpt = lessonRepository.findByIdAndCourseId(lessonId, courseId);
            if (lessonOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "status", "error",
                        "message", "Lesson not found"
                ));
            }
            Lesson lesson = lessonOpt.get();
            Optional<UserLesson> userLessonOpt = userLessonRepository.findByUserIdAndLessonId(userId, lessonId);
            boolean wasCompleted = userLessonOpt.map(UserLesson::getCompleted).orElse(false);
            UserLesson userLesson = userLessonOpt.orElseGet(() -> {
                UserLesson ul = new UserLesson();
                ul.setUserId(userId);
                ul.setLessonId(lessonId);
                return ul;
            });
            if (!wasCompleted) {
                userLesson.setCompleted(true);
                userLesson.setCompletedAt(LocalDateTime.now());
                userLessonRepository.save(userLesson);

                userRepository.findById(userId).ifPresent(u -> {
                    u.setPoints(u.getPoints() + lesson.getPoints());
                    userRepository.save(u);
                });
            }
            Long completedCount = userLessonRepository.countByUserIdAndCourseIdAndCompletedTrue(userId, courseId);
            Long totalCount = lessonRepository.countByCourseId(courseId);
            int progress = totalCount > 0 ? (int) Math.round((completedCount * 100.0) / totalCount) : 0;
            boolean isCompleted = progress >= 100;
            Optional<UserCourse> userCourseOpt = userCourseRepository.findByUserIdAndCourseId(userId, courseId);
            UserCourse userCourse = userCourseOpt.orElseGet(() -> {
                UserCourse uc = new UserCourse();
                uc.setUserId(userId);
                uc.setCourseId(courseId);
                return uc;
            });
            userCourse.setProgress(progress);
            userCourse.setCompleted(isCompleted);
            userCourse.setLastAccessed(LocalDateTime.now());
            userCourseRepository.save(userCourse);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Lesson marked as completed",
                    "progress", progress,
                    "course_completed", isCompleted
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage() != null ? e.getMessage() : "Unexpected error"
            ));
        }
    }

    @GetMapping("/{courseId}/lessons/{lessonId}")
    public ResponseEntity<?> getLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            Optional<Lesson> lessonOpt = lessonRepository.findByIdAndCourseId(lessonId, courseId);
            if (lessonOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "status", "error",
                        "message", "Lesson not found"
                ));
            }
            Lesson lesson = lessonOpt.get();
            Optional<UserCourse> userCourseOpt = userCourseRepository.findByUserIdAndCourseId(userId, courseId);
            if (userCourseOpt.isEmpty()) {
                UserCourse uc = new UserCourse();
                uc.setUserId(userId);
                uc.setCourseId(courseId);
                uc.setLastAccessed(LocalDateTime.now());
                userCourseRepository.save(uc);
            } else {
                UserCourse uc = userCourseOpt.get();
                uc.setLastAccessed(LocalDateTime.now());
                userCourseRepository.save(uc);
            }
            Optional<UserLesson> userLessonOpt = userLessonRepository.findByUserIdAndLessonId(userId, lessonId);
            boolean completed = userLessonOpt.map(UserLesson::getCompleted).orElse(false);
            LocalDateTime completedAt = userLessonOpt.map(UserLesson::getCompletedAt).orElse(null);
            Long prevId = lessonRepository.findPreviousLessonId(courseId, lesson.getOrderNumber());
            Long nextId = lessonRepository.findNextLessonId(courseId, lesson.getOrderNumber());
            Map<String, Object> completion = new HashMap<>();
            completion.put("completed", completed);
            completion.put("completed_at", completedAt);
            Map<String, Object> navigation = new HashMap<>();
            navigation.put("previous_id", prevId);
            navigation.put("next_id", nextId);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("lesson", lesson);
            response.put("completion", completion);
            response.put("navigation", navigation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", e.getMessage() != null ? e.getMessage() : "Unexpected error"
            ));
        }
    }

    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<?> createLesson(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        Long teacherId = authService.getUserIdFromAuthHeader(authHeader);
        return ResponseEntity.ok(courseService.createLesson(courseId, body, teacherId));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseDetails(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseDetails(courseId));
    }
}
