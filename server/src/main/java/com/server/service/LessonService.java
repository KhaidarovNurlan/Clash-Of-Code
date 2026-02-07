package com.server.service;

import com.server.model.Lesson;
import com.server.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LessonService {

    @Autowired private LessonRepository lessonRepository;

    public Lesson getLesson(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public List<Lesson> getLessonsForCourse(Long courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    public Map<String,Object> completeLesson(Long userId, Long courseId, Long lessonId) {
        throw new UnsupportedOperationException("completeLesson: implement user_lessons repository or custom query");
    }
}
