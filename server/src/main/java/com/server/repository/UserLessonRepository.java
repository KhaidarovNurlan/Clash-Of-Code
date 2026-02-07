package com.server.repository;

import com.server.model.UserLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserLessonRepository extends JpaRepository<UserLesson, Long> {
    @Query(value = """
        SELECT EXISTS (
            SELECT 1 FROM user_lessons 
            WHERE user_id = :userId AND lesson_id = :lessonId AND completed = true
        )
    """, nativeQuery = true)
    boolean isLessonCompleted(@Param("userId") Long userId, @Param("lessonId") Long lessonId);

    Optional<UserLesson> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("""
        SELECT COUNT(ul) FROM UserLesson ul 
        JOIN Lesson l ON ul.lessonId = l.id 
        WHERE ul.userId = :userId AND l.course.id = :courseId AND ul.completed = true
    """)
    Long countByUserIdAndCourseIdAndCompletedTrue(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
