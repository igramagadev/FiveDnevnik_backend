package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.GradeView;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface GradeViewRepository extends JpaRepository<GradeView, Long> {

    List<GradeView> findByStudentId(Long studentId);

    List<GradeView> findByStudentIdAndDateBetween(Long studentId, LocalDateTime from, LocalDateTime to);

    List<GradeView> findBySubjectId(Long subjectId);

    List<GradeView> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    @Query("SELECT g FROM GradeView g JOIN g.student s WHERE s.className = :className AND g.subjectId = :subjectId")
    List<GradeView> findByClassNameAndSubjectId(@Param("className") String className, @Param("subjectId") Long subjectId);

    @Query("SELECT g FROM GradeView g JOIN g.student s WHERE s.className = :className")
    List<GradeView> findByClassName(@Param("className") String className);

    List<GradeView> findByStudentIdAndIsFinalTrueAndPeriod(Long studentId, String period);

    List<GradeView> findByStudentIdAndSubjectIdAndPeriod(Long studentId, Long subjectId, String period);
} 