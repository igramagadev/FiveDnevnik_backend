package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {

    List<AcademicPeriod> findByType(String type);

    List<AcademicPeriod> findByAcademicYear(String academicYear);

    List<AcademicPeriod> findByTypeAndAcademicYear(String type, String academicYear);

    Optional<AcademicPeriod> findByIsCurrentTrue();

    @Query("SELECT p FROM AcademicPeriod p WHERE :date BETWEEN p.startDate AND p.endDate")
    List<AcademicPeriod> findByDate(LocalDate date);

    @Query("SELECT p FROM AcademicPeriod p WHERE :date BETWEEN p.startDate AND p.endDate AND p.type = :type")
    Optional<AcademicPeriod> findByDateAndType(LocalDate date, String type);

    Optional<AcademicPeriod> findByOrderNumberAndTypeAndAcademicYear(Integer orderNumber, String type, String academicYear);
} 