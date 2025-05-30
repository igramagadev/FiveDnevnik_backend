package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.Class;
import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с классами
 */
@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    
    /**
     * Проверка существования класса по имени
     */
    boolean existsByName(String name);
    
    /**
     * Поиск класса по имени
     */
    Optional<Class> findByName(String name);
    
    /**
     * Поиск классов по учебному году
     */
    List<Class> findByAcademicYear(String academicYear);
    
    /**
     * Поиск классов по классному руководителю
     */
    List<Class> findByClassTeacher(User classTeacher);
} 