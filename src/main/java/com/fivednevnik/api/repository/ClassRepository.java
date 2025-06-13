package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.Class;
import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

    boolean existsByName(String name);

    Optional<Class> findByName(String name);

    List<Class> findByAcademicYear(String academicYear);

    List<Class> findByClassTeacher(User classTeacher);
} 
