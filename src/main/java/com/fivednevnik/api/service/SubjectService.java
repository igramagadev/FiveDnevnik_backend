package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.SubjectDto;

import java.util.List;

/**
 * Интерфейс сервиса для работы с предметами
 */
public interface SubjectService {
    
    /**
     * Получить все предметы
     */
    List<SubjectDto> getAllSubjects();
    
    /**
     * Получить предмет по ID
     */
    SubjectDto getSubjectById(Long id);
    
    /**
     * Создать новый предмет
     */
    SubjectDto createSubject(SubjectDto subjectDto);
    
    /**
     * Обновить существующий предмет
     */
    SubjectDto updateSubject(Long id, SubjectDto subjectDto);
    
    /**
     * Удалить предмет
     */
    void deleteSubject(Long id);
    
} 