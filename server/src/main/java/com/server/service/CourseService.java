package com.server.service;

import com.server.model.Course;
import com.server.model.Lesson;
import com.server.model.User;
import com.server.repository.CourseRepository;
import com.server.repository.LessonRepository;
import com.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    @Autowired private CourseRepository courseRepository;
    @Autowired private LessonRepository lessonRepository;
    @Autowired private UserRepository userRepository;

    public List<Course> getCourses(String level, int limit, int offset) {
        return courseRepository.findCourses(level, limit, offset);
    }

    public long countCourses(String level) {
        return courseRepository.countCourses(level);
    }

     public Map<String, Object> getCourseDetails(Long courseId) {
        Map<String, Object> course = courseRepository.findCourseDetails(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<Map<String, Object>> lessons = lessonRepository.findLessonSummariesByCourseId(courseId);
        return Map.of(
                "course", course,
                "lessons", lessons
        );
    }

    public Map<String,Object> createCourse(Long teacherId, Map<String,Object> body) {
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new RuntimeException("Teacher not found"));
        Course course = new Course();
        course.setTitle((String) body.get("title"));
        course.setDescription((String) body.get("description"));
        course.setLevel((String) body.get("level"));
        course.setCreatedBy(teacher);
        courseRepository.save(course);
        return Map.of("status","success","courseId", course.getId());
    }

    public Map<String,Object> createLesson(Long courseId, Map<String,Object> body, Long teacherId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        if (!course.getCreatedBy().getId().equals(teacherId)) throw new RuntimeException("Not course owner");
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle((String) body.get("title"));
        lesson.setContent((String) body.get("content"));
        Object ord = body.get("order_number");
        lesson.setOrderNumber(ord instanceof Integer ? (Integer) ord : Integer.parseInt(String.valueOf(ord)));
        Object pts = body.getOrDefault("points", 0);
        lesson.setPoints(pts instanceof Integer ? (Integer) pts : Integer.parseInt(String.valueOf(pts)));
        lessonRepository.save(lesson);
        return Map.of("status","success","lessonId", lesson.getId());
    }
}
