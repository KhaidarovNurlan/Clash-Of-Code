package com.server.repository;

import com.server.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course,Long> {
    @Query(value = """
        SELECT * FROM courses 
        WHERE (:level IS NULL OR level ILIKE %:level%) 
        ORDER BY id DESC LIMIT :limit OFFSET :offset
    """, nativeQuery=true)
    List<Course> findCourses(@Param("level") String level, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = """
        SELECT COUNT(*) FROM courses 
        WHERE (:level IS NULL OR level ILIKE %:level%)
    """, nativeQuery=true)
    long countCourses(@Param("level") String level);
    
    @Query("""
        SELECT new map(
            c.id as id,
            c.title as title,
            c.description as description,
            c.level as level,
            u.username as created_by_username
        )
        FROM Course c
        LEFT JOIN User u ON c.createdBy.id = u.id
        WHERE c.id = :courseId
    """)
    Optional<Map<String, Object>> findCourseDetails(@Param("courseId") Long courseId);
}
