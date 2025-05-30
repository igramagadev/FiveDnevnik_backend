package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.SubjectDto;
import com.fivednevnik.api.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с предметами
 */
@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
@Slf4j
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * Получение списка всех предметов
     */
    @GetMapping
    public ResponseEntity<?> getAllSubjects() {
        log.debug("Запрос на получение всех предметов");
        
        try {
            List<SubjectDto> subjects = subjectService.getAllSubjects();
            log.info("Получен список предметов, количество: {}", subjects.size());
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            log.error("Ошибка при получении списка предметов", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка предметов: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Получение информации о конкретном предмете
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        log.debug("Запрос на получение предмета с ID: {}", id);
        
        try {
            SubjectDto subjectDto = subjectService.getSubjectById(id);
            log.info("Получена информация о предмете: {}", subjectDto.getName());
            return ResponseEntity.ok(subjectDto);
        } catch (Exception e) {
            log.error("Ошибка при получении информации о предмете с ID: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении информации о предмете: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Создание нового предмета (только для администраторов)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSubject(@RequestBody SubjectDto subjectDto) {
        log.debug("Запрос на создание нового предмета: {}", subjectDto.getName());
        
        try {
            SubjectDto createdSubject = subjectService.createSubject(subjectDto);
            log.info("Создан новый предмет: {}", createdSubject.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
        } catch (Exception e) {
            log.error("Ошибка при создании нового предмета", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при создании предмета: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Обновление информации о предмете (только для администраторов)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody SubjectDto subjectDto) {
        log.debug("Запрос на обновление предмета с ID: {}", id);
        
        try {
            SubjectDto updatedSubject = subjectService.updateSubject(id, subjectDto);
            log.info("Обновлен предмет: {}", updatedSubject.getName());
            return ResponseEntity.ok(updatedSubject);
        } catch (Exception e) {
            log.error("Ошибка при обновлении предмета с ID: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при обновлении предмета: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Удаление предмета (только для администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        log.debug("Запрос на удаление предмета с ID: {}", id);
        
        try {
            subjectService.deleteSubject(id);
            log.info("Удален предмет с ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ошибка при удалении предмета с ID: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при удалении предмета: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
} 