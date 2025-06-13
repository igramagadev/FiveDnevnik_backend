package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.GradeTableDto;
import com.fivednevnik.api.service.GradeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/grade-tables")
@RequiredArgsConstructor
public class GradeTableController {

    private final GradeTableService gradeTableService;

    @GetMapping("/class/{className}/subject/{subjectId}/period/{period}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeTableDto> getGradeTableByClassAndSubjectAndPeriod(
            @PathVariable String className,
            @PathVariable Long subjectId,
            @PathVariable String period) {
        return ResponseEntity.ok(gradeTableService.getGradeTableByClassAndSubjectAndPeriod(
                className, subjectId, period));
    }

    @GetMapping("/class/{className}/subject/{subjectId}/academic-period/{academicPeriodId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeTableDto> getGradeTableByClassAndSubjectAndAcademicPeriod(
            @PathVariable String className,
            @PathVariable Long subjectId,
            @PathVariable Long academicPeriodId) {
        return ResponseEntity.ok(gradeTableService.getGradeTableByClassAndSubjectAndAcademicPeriod(
                className, subjectId, academicPeriodId));
    }

    @GetMapping("/class/{className}/subject/{subjectId}/date-range")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<GradeTableDto> getGradeTableByClassAndSubjectAndDateRange(
            @PathVariable String className,
            @PathVariable Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(gradeTableService.getGradeTableByClassAndSubjectAndDateRange(
                className, subjectId, startDate, endDate));
    }
} 