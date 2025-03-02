package com.fivednevnik.backend.controller;

import com.fivednevnik.backend.dto.GradeDto;
import com.fivednevnik.backend.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or @userSecurity.isCurrentUser(#studentId)")
    public ResponseEntity<List<GradeDto>> getGradesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or @userSecurity.isCurrentUser(#studentId)")
    public ResponseEntity<List<GradeDto>> getGradesByStudentAndSubject(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        return ResponseEntity.ok(gradeService.getGradesByStudentAndSubject(studentId, subjectId));
    }

    @GetMapping("/student/{studentId}/date-range")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER') or @userSecurity.isCurrentUser(#studentId)")
    public ResponseEntity<List<GradeDto>> getGradesByStudentAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(gradeService.getGradesByStudentAndDateRange(studentId, startDate, endDate));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<GradeDto> createGrade(@RequestBody GradeDto gradeDto) {
        return ResponseEntity.ok(gradeService.createGrade(gradeDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<GradeDto> updateGrade(@PathVariable Long id, @RequestBody GradeDto gradeDto) {
        return ResponseEntity.ok(gradeService.updateGrade(id, gradeDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok().build();
    }
} 