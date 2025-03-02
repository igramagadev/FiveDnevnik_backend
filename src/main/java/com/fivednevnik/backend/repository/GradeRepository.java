package com.fivednevnik.backend.repository;

import com.fivednevnik.backend.model.Grade;
import com.fivednevnik.backend.model.Lesson;
import com.fivednevnik.backend.model.Subject;
import com.fivednevnik.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent(User student);
    List<Grade> findByTeacher(User teacher);
    List<Grade> findBySubject(Subject subject);
    List<Grade> findByLesson(Lesson lesson);
    List<Grade> findByStudentAndSubject(User student, Subject subject);
    List<Grade> findByStudentAndDateBetween(User student, LocalDate start, LocalDate end);
    List<Grade> findByStudentAndSubjectAndDateBetween(User student, Subject subject, LocalDate start, LocalDate end);
} 