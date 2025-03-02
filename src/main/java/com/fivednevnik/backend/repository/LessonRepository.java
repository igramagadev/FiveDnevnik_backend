package com.fivednevnik.backend.repository;

import com.fivednevnik.backend.model.Lesson;
import com.fivednevnik.backend.model.SchoolClass;
import com.fivednevnik.backend.model.Subject;
import com.fivednevnik.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByTeacher(User teacher);
    List<Lesson> findBySchoolClass(SchoolClass schoolClass);
    List<Lesson> findBySubject(Subject subject);
    List<Lesson> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Lesson> findBySchoolClassAndStartTimeBetween(SchoolClass schoolClass, LocalDateTime start, LocalDateTime end);
    List<Lesson> findByTeacherAndStartTimeBetween(User teacher, LocalDateTime start, LocalDateTime end);
} 