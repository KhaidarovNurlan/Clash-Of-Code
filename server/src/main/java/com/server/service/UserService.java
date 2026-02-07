package com.server.service;

import com.server.model.User;
import com.server.model.UserCourse;
import com.server.model.TournamentSubmission;
import com.server.repository.UserRepository;
import com.server.repository.UserCourseRepository;
import com.server.repository.LessonRepository;
import com.server.repository.UserLessonRepository;
import com.server.repository.TournamentSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserCourseRepository userCourseRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private UserLessonRepository userLessonRepository;
    @Autowired private TournamentSubmissionRepository tournamentSubmissionRepository;

    public Optional<User> getUserProfile(Long userId) {
        return userRepository.findById(userId);
    }

    public Map<String,Object> updateProfile(Long userId, String username) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (username != null && !username.isBlank()) user.setUsername(username);
        userRepository.save(user);
        return Map.of("status","success","user", user);
    }

    public Map<String, Object> getCourseProgress(Long userId, Long courseId) {
        UserCourse uc = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> {
                    UserCourse newUC = new UserCourse();
                    newUC.setUserId(userId);
                    newUC.setCourseId(courseId);
                    newUC.setProgress(0);
                    newUC.setCompleted(false);
                    newUC.setLastAccessed(LocalDateTime.now());
                    return userCourseRepository.save(newUC);
                });
        List<Map<String, Object>> lessons = lessonRepository.findByCourseId(courseId)
            .stream()
            .map(l -> {
                boolean completed = userLessonRepository.isLessonCompleted(userId, l.getId());
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", l.getId());
                map.put("title", l.getTitle());
                map.put("order_number", l.getOrderNumber());
                map.put("completed", completed);
                return map;
            })
            .toList();
        return Map.of(
                "progress", uc.getProgress(),
                "completed", uc.getCompleted(),
                "last_accessed", uc.getLastAccessed(),
                "lessons", lessons
        );
    }

    public Map<String, Object> getUserSubmissions(Long userId, int limit, int offset) {
        List<TournamentSubmission> submissions = tournamentSubmissionRepository.findByUserId(userId);
        long total = tournamentSubmissionRepository.countByUserId(userId);
        return Map.of(
                "total", total,
                "submissions", submissions
        );
    }
}
