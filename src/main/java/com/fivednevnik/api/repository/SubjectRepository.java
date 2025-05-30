package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с предметами
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    /**
     * Найти предмет по имени
     */
    Optional<Subject> findByName(String name);
    
    /**
     * Проверить существование предмета с указанным именем
     */
    boolean existsByName(String name);
    
} 