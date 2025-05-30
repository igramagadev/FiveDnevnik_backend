package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.GradeDto;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для работы с оценками
 */
@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    private final GradeService gradeService;

    /**
     * Получение оценок текущего пользователя (для учеников)
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<GradeDto>> getMyGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Запрос оценок для ученика: {}", username);
        return ResponseEntity.ok(gradeService.getGradesByStudentUsername(username));
    }

    /**
     * Получение оценок за период (для учеников)
     */
    @GetMapping("/my/period")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<GradeDto>> getMyGradesByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Запрос оценок для ученика {} за период с {} по {}", username, from, to);
        return ResponseEntity.ok(gradeService.getGradesByStudentUsernameAndPeriod(username, from, to));
    }

    /**
     * Получение оценок по ID ученика (для учителей и администраторов)
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesByStudentId(@PathVariable Long studentId) {
        log.info("Запрос оценок для ученика с ID: {}", studentId);
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId));
    }

    /**
     * Получение оценок по ID предмета (для учителей и администраторов)
     */
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesBySubjectId(@PathVariable Long subjectId) {
        log.info("Запрос оценок по предмету с ID: {}", subjectId);
        return ResponseEntity.ok(gradeService.getGradesBySubjectId(subjectId));
    }

    /**
     * Получение оценок по классу (для учителей и администраторов)
     */
    @GetMapping("/class/{className}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getGradesByClassName(@PathVariable String className) {
        log.info("Запрос оценок для класса: {}", className);
        return ResponseEntity.ok(gradeService.getGradesByClassName(className));
    }

    /**
     * Создание новой оценки (для учителей и администраторов)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeDto> createGrade(@Valid @RequestBody GradeDto gradeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        log.info("Создание новой оценки учителем {}: {}", teacherUsername, gradeDto);
        return ResponseEntity.ok(gradeService.createGrade(gradeDto, teacherUsername));
    }

    /**
     * Обновление оценки (для учителей и администраторов)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeDto> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody GradeDto gradeDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        log.info("Обновление оценки с ID {} учителем {}", id, teacherUsername);
        return ResponseEntity.ok(gradeService.updateGrade(id, gradeDto, teacherUsername));
    }

    /**
     * Удаление оценки (для учителей и администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        log.info("Удаление оценки с ID {} учителем {}", id, teacherUsername);
        gradeService.deleteGrade(id, teacherUsername);
        return ResponseEntity.noContent().build();
    }
} 