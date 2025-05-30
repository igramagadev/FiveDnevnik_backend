package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.GradeDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с оценками
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {
    
    private final UserRepository userRepository;
    
    /**
     * Получение оценок по имени пользователя
     */
    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentUsername(String username) {
        log.debug("Получение оценок для ученика: {}", username);
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        // TODO: Реализовать получение оценок из репозитория
        return getMockGrades(student.getId());
    }
    
    /**
     * Получение оценок по имени пользователя за период
     */
    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentUsernameAndPeriod(String username, LocalDate from, LocalDate to) {
        log.debug("Получение оценок для ученика {} за период с {} по {}", username, from, to);
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        // TODO: Реализовать получение оценок из репозитория
        return getMockGrades(student.getId());
    }
    
    /**
     * Получение оценок по ID ученика
     */
    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentId(Long studentId) {
        log.debug("Получение оценок для ученика с ID: {}", studentId);
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Пользователь не найден с ID: " + studentId);
        }
        
        // TODO: Реализовать получение оценок из репозитория
        return getMockGrades(studentId);
    }
    
    /**
     * Получение оценок по ID предмета
     */
    @Transactional(readOnly = true)
    public List<GradeDto> getGradesBySubjectId(Long subjectId) {
        log.debug("Получение оценок по предмету с ID: {}", subjectId);
        // TODO: Реализовать получение оценок из репозитория
        return new ArrayList<>();
    }
    
    /**
     * Получение оценок по имени класса
     */
    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByClassName(String className) {
        log.debug("Получение оценок для класса: {}", className);
        // TODO: Реализовать получение оценок из репозитория
        return new ArrayList<>();
    }
    
    /**
     * Создание новой оценки
     */
    @Transactional
    public GradeDto createGrade(GradeDto gradeDto, String teacherUsername) {
        log.debug("Создание новой оценки учителем {}: {}", teacherUsername, gradeDto);
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));
        

        if (gradeDto.getTeacherId() == null) {
            gradeDto.setTeacherId(teacher.getId());
        }
        
        // TODO: Реализовать сохранение оценки в репозиторий
        gradeDto.setId(1L);
        return gradeDto;
    }
    
    /**
     * Обновление оценки
     */
    @Transactional
    public GradeDto updateGrade(Long id, GradeDto gradeDto, String teacherUsername) {
        log.debug("Обновление оценки с ID {} учителем {}", id, teacherUsername);
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));
        
        // TODO: Реализовать обновление оценки в репозитории
        gradeDto.setId(id);
        return gradeDto;
    }
    
    /**
     * Удаление оценки
     */
    @Transactional
    public void deleteGrade(Long id, String teacherUsername) {
        log.debug("Удаление оценки с ID {} учителем {}", id, teacherUsername);
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));
        
        // TODO: Реализовать удаление оценки из репозитория
    }
    
    /**
     * Временный метод для создания тестовых данных
     */
    private List<GradeDto> getMockGrades(Long studentId) {
        List<GradeDto> grades = new ArrayList<>();
        
        // Математика
        grades.add(GradeDto.builder()
                .id(1L)
                .studentId(studentId)
                .studentName("Иван Смирнов")
                .subjectId(1L)
                .subjectName("Математика")
                .teacherId(2L)
                .teacherName("Елена Петрова")
                .value(5)
                .weight(1.0f)
                .description("Контрольная работа")
                .date(LocalDateTime.of(2024, 5, 15, 10, 0))
                .build());
        
        grades.add(GradeDto.builder()
                .id(2L)
                .studentId(studentId)
                .studentName("Иван Смирнов")
                .subjectId(1L)
                .subjectName("Математика")
                .teacherId(2L)
                .teacherName("Елена Петрова")
                .value(4)
                .weight(1.0f)
                .description("Домашнее задание")
                .date(LocalDateTime.of(2024, 5, 17, 10, 0))
                .build());
        
        // Русский язык
        grades.add(GradeDto.builder()
                .id(3L)
                .studentId(studentId)
                .studentName("Иван Смирнов")
                .subjectId(2L)
                .subjectName("Русский язык")
                .teacherId(3L)
                .teacherName("Сергей Иванов")
                .value(4)
                .weight(1.0f)
                .description("Диктант")
                .date(LocalDateTime.of(2024, 5, 16, 11, 0))
                .build());
        
        grades.add(GradeDto.builder()
                .id(4L)
                .studentId(studentId)
                .studentName("Иван Смирнов")
                .subjectId(2L)
                .subjectName("Русский язык")
                .teacherId(3L)
                .teacherName("Сергей Иванов")
                .value(5)
                .weight(1.5f)
                .description("Сочинение")
                .date(LocalDateTime.of(2024, 5, 18, 11, 0))
                .build());
        
        return grades;
    }
} 