package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.Attendance;
import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent(User student);

    @Query("SELECT a FROM Attendance a JOIN a.student s WHERE s.className = :className")
    List<Attendance> findByClassName(@Param("className") String className);

    @Query("SELECT a FROM Attendance a JOIN a.student s WHERE s.classId = :classId")
    List<Attendance> findByClassId(@Param("classId") Long classId);

    List<Attendance> findByStudentAndDate(User student, LocalDate date);

    @Query("SELECT a FROM Attendance a JOIN a.student s WHERE s.className = :className AND a.date = :date")
    List<Attendance> findByClassNameAndDate(@Param("className") String className, @Param("date") LocalDate date);

    List<Attendance> findByStudentAndDateBetween(User student, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT a FROM Attendance a JOIN a.student s WHERE s.className = :className AND a.date BETWEEN :fromDate AND :toDate")
    List<Attendance> findByClassNameAndDateBetween(
            @Param("className") String className, 
            @Param("fromDate") LocalDate fromDate, 
            @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.period = :period AND a.isPresent = :isPresent")
    int countByStudentAndPeriodAndIsPresent(
            @Param("student") User student, 
            @Param("period") String period, 
            @Param("isPresent") boolean isPresent);

    @Query("SELECT COUNT(a) FROM Attendance a JOIN a.student s WHERE s.className = :className AND a.period = :period AND a.isPresent = :isPresent")
    int countByClassNameAndPeriodAndIsPresent(
            @Param("className") String className, 
            @Param("period") String period, 
            @Param("isPresent") boolean isPresent);
} 
