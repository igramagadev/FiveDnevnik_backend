package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.HomeworkDto;
import com.fivednevnik.api.dto.HomeworkStatusDto;
import com.fivednevnik.api.service.HomeworkService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/homework")
@RequiredArgsConstructor

public class HomeworkController {

    private final HomeworkService homeworkService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<HomeworkDto>> getMyHomework(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (from != null && to != null) {
            return ResponseEntity.ok(homeworkService.getHomeworkForStudentByPeriod(username, from, to));
        }
        return ResponseEntity.ok(homeworkService.getHomeworkForStudent(username));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDto>> getHomeworkByClassId(@PathVariable Long classId) {
        return ResponseEntity.ok(homeworkService.getHomeworkByClassId(classId));
    }

    @GetMapping("/class/name/{className}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDto>> getHomeworkByClassName(@PathVariable String className) {
        return ResponseEntity.ok(homeworkService.getHomeworkByClassName(className));
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkDto>> getHomeworkBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(homeworkService.getHomeworkBySubjectId(subjectId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<HomeworkDto> createHomework(@Valid @RequestBody HomeworkDto homeworkDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        return ResponseEntity.ok(homeworkService.createHomework(homeworkDto, teacherUsername));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<HomeworkDto> updateHomework(
            @PathVariable Long id,
            @Valid @RequestBody HomeworkDto homeworkDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        return ResponseEntity.ok(homeworkService.updateHomework(id, homeworkDto, teacherUsername));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteHomework(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherUsername = authentication.getName();
        homeworkService.deleteHomework(id, teacherUsername);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<HomeworkStatusDto> updateHomeworkStatus(
            @PathVariable Long id,
            @Valid @RequestBody HomeworkStatusDto statusDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentUsername = authentication.getName();
        return ResponseEntity.ok(homeworkService.updateHomeworkStatus(id, statusDto, studentUsername));
    }

    @GetMapping("/{homeworkId}/status")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<HomeworkStatusDto>> getHomeworkStatuses(@PathVariable Long homeworkId) {
        return ResponseEntity.ok(homeworkService.getHomeworkStatuses(homeworkId));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'CLASS_TEACHER', 'TEACHER', 'PARENT')")
    public ResponseEntity<List<HomeworkDto>> getActiveHomework() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(List.of());
    }
} 
