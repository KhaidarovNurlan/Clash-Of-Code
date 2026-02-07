package com.server.repository;

import com.server.model.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    @Query(value = """
        SELECT * FROM user_courses 
        WHERE user_id = :userId 
        ORDER BY last_accessed DESC
    """, nativeQuery = true)
    List<UserCourse> findByUserId(Long userId);

    @Query(value = """
        SELECT * FROM user_courses 
        WHERE user_id = :userId AND course_id = :courseId
    """, nativeQuery = true)
    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);
}
