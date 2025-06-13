package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.AcademicReportDto;
import com.fivednevnik.api.service.AcademicReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/academic-reports")
@RequiredArgsConstructor
public class AcademicReportController {

    private final AcademicReportService academicReportService;

    @GetMapping("/class/{className}/period/{periodId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<AcademicReportDto> getClassReport(
            @PathVariable String className,
            @PathVariable Long periodId) {
        return ResponseEntity.ok(academicReportService.generateClassReport(className, periodId));
    }

    @GetMapping("/student/{studentId}/period/{periodId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN', 'STUDENT', 'PARENT')")
    public ResponseEntity<AcademicReportDto.StudentStatistics> getStudentReport(
            @PathVariable Long studentId,
            @PathVariable Long periodId) {
        return ResponseEntity.ok(academicReportService.generateStudentReport(studentId, periodId));
    }
} 