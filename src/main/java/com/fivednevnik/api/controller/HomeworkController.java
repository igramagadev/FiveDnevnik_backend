package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.HomeworkDto;
import com.fivednevnik.api.dto.HomeworkStatusDto;
import com.fivednevnik.api.service.HomeworkService;
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
 * Контроллер для работы с домашними заданиями
 */
@RestController
@RequestMapping("/homework")
@RequiredArgsConstructor
@Slf4j
public class HomeworkController {

    private final HomeworkService homeworkService;

    /**
     * Получение домашних заданий для текущего пользователя
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<HomeworkDto>> getMyHomework(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Запрос домашних заданий для ученика: {} (период: {} - {})", username, from, to);
        
        if (from != null && to != null) {
            return ResponseEntity.ok(homeworkService.getHomeworkForStudentByPeriod(username, from, to));
        }
        return ResponseEntity.ok(homeworkService.getHomeworkForStudent(username));
    }

    /**
     * Получение домашних заданий по ID класса
     */
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDto>> getHomeworkByClassId(@PathVariable Long classId) {
        log.info("Запрос домашних заданий для класса с ID: {}", classId);
        return ResponseEntity.ok(homeworkService.getHomeworkByClassId(classId));
    }

    /**
     * Получение домашних заданий по имени класса
     */
    @GetMapping("/class/name/{className}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDto>> getHomeworkByClassName(@PathVariable String className) {
        log.info("Запрос домашних заданий для класса: {}", className);
        return ResponseEntity.ok(homeworkService.getHomeworkByClassName(className));
    }

    /**
     * Получение домашних заданий по ID предмета
     */
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDto>> getHomeworkBySubjectId(@PathVariable Long subjectId) {
        log.info("Запрос домашних заданий по предмету с ID: {}", subjectId);
        return ResponseEntity.ok(homeworkService.getHomeworkBySubjectId(subjectId));
    }

    /**
     * Создание нового домашнего задания (для учителей и администраторов)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<HomeworkDto> createHomework(@Valid @RequestBody HomeworkDto homeworkDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        log.info("Создание нового домашнего задания учителем {}: {}", teacherUsername, homeworkDto);
        return ResponseEntity.ok(homeworkService.createHomework(homeworkDto, teacherUsername));
    }

    /**
     * Обновление домашнего задания (для учителей и администраторов)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<HomeworkDto> updateHomework(
            @PathVariable Long id,
            @Valid @RequestBody HomeworkDto homeworkDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        log.info("Обновление домашнего задания с ID {} учителем {}", id, teacherUsername);
        return ResponseEntity.ok(homeworkService.updateHomework(id, homeworkDto, teacherUsername));
    }

    /**
     * Удаление домашнего задания (для учителей и администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteHomework(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        log.info("Удаление домашнего задания с ID {} учителем {}", id, teacherUsername);
        homeworkService.deleteHomework(id, teacherUsername);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновление статуса выполнения домашнего задания (для учеников)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<HomeworkStatusDto> updateHomeworkStatus(
            @PathVariable Long id,
            @Valid @RequestBody HomeworkStatusDto statusDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentUsername = authentication.getName();
        log.info("Обновление статуса домашнего задания с ID {} учеником {}", id, studentUsername);
        return ResponseEntity.ok(homeworkService.updateHomeworkStatus(id, statusDto, studentUsername));
    }

    /**
     * Получение статусов домашних заданий для класса (для учителей и администраторов)
     */
    @GetMapping("/{homeworkId}/status")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkStatusDto>> getHomeworkStatuses(@PathVariable Long homeworkId) {
        log.info("Запрос статусов выполнения домашнего задания с ID: {}", homeworkId);
        return ResponseEntity.ok(homeworkService.getHomeworkStatuses(homeworkId));
    }
} 