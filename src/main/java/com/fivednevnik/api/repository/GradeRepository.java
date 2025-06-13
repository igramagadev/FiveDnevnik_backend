package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.Grade;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudent(User student);

    List<Grade> findByTeacher(User teacher);

    List<Grade> findByStudentAndDateBetween(User student, LocalDateTime from, LocalDateTime to);

    List<Grade> findBySubject(Subject subject);

    List<Grade> findByStudentAndSubject(User student, Subject subject);

    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.className = :className AND g.subject = :subject")
    List<Grade> findByClassNameAndSubject(@Param("className") String className, @Param("subject") Subject subject);

    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.className = :className")
    List<Grade> findByClassName(@Param("className") String className);

    List<Grade> findByStudentAndIsFinalTrueAndPeriod(User student, String period);

    List<Grade> findByStudentAndSubjectAndPeriod(User student, Subject subject, String period);

    List<Grade> findByStudentAndSubjectAndDateBetween(User student, Subject subject, LocalDateTime from, LocalDateTime to);

    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.className = :className AND g.period = :period")
    List<Grade> findByClassNameAndPeriod(@Param("className") String className, @Param("period") String period);

    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.className = :className AND g.subject = :subject AND g.period = :period")
    List<Grade> findByClassNameAndSubjectAndPeriod(@Param("className") String className, @Param("subject") Subject subject, @Param("period") String period);

    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.className = :className AND g.subject = :subject AND g.date BETWEEN :from AND :to")
    List<Grade> findByClassNameAndSubjectAndDateBetween(@Param("className") String className, @Param("subject") Subject subject, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<Grade> findBySubjectAndPeriod(Subject subject, String period);

    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.className = :className AND g.isFinal = true AND g.period = :period")
    List<Grade> findByClassNameAndIsFinalTrueAndPeriod(@Param("className") String className, @Param("period") String period);

    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.className = :className AND g.subject = :subject AND g.isFinal = true AND g.period = :period")
    List<Grade> findByClassNameAndSubjectAndIsFinalTrueAndPeriod(@Param("className") String className, @Param("subject") Subject subject, @Param("period") String period);
}
