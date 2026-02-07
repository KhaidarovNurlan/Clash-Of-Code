package com.server.repository;

import com.server.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query(value = """
        SELECT * FROM lessons 
        WHERE course_id = :courseId 
        ORDER BY order_number
    """, nativeQuery = true)
    List<Lesson> findByCourseId(Long courseId);

    Optional<Lesson> findByIdAndCourseId(Long id, Long courseId);

    @Query("""
        SELECT l.id FROM Lesson l 
        WHERE l.course.id = :courseId AND l.orderNumber < :orderNumber 
        ORDER BY l.orderNumber DESC LIMIT 1
    """)
    Long findPreviousLessonId(@Param("courseId") Long courseId, @Param("orderNumber") int orderNumber);

    @Query("""
        SELECT l.id FROM Lesson l 
        WHERE l.course.id = :courseId AND l.orderNumber > :orderNumber 
        ORDER BY l.orderNumber ASC LIMIT 1
    """)
    Long findNextLessonId(@Param("courseId") Long courseId, @Param("orderNumber") int orderNumber);

    Long countByCourseId(Long courseId);

    @Query("""
        SELECT new map(
            l.id as id,
            l.title as title,
            l.orderNumber as order_number,
            l.points as points
        )
        FROM Lesson l
        WHERE l.course.id = :courseId
        ORDER BY l.orderNumber
    """)
    List<Map<String, Object>> findLessonSummariesByCourseId(@Param("courseId") Long courseId);

}
