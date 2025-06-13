package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.AcademicPeriodDto;
import com.fivednevnik.api.service.AcademicPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/academic-periods")
@RequiredArgsConstructor
public class AcademicPeriodController {

    private final AcademicPeriodService academicPeriodService;

    @GetMapping
    public ResponseEntity<List<AcademicPeriodDto>> getAllPeriods() {
        return ResponseEntity.ok(academicPeriodService.getAllPeriods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicPeriodDto> getPeriodById(@PathVariable Long id) {
        return ResponseEntity.ok(academicPeriodService.getPeriodById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<AcademicPeriodDto>> getPeriodsByType(@PathVariable String type) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByType(type));
    }

    @GetMapping("/academic-year/{academicYear}")
    public ResponseEntity<List<AcademicPeriodDto>> getPeriodsByAcademicYear(@PathVariable String academicYear) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByAcademicYear(academicYear));
    }


    @GetMapping("/type/{type}/academic-year/{academicYear}")
    public ResponseEntity<List<AcademicPeriodDto>> getPeriodsByTypeAndAcademicYear(
            @PathVariable String type,
            @PathVariable String academicYear) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByTypeAndAcademicYear(type, academicYear));
    }


    @GetMapping("/current")
    public ResponseEntity<AcademicPeriodDto> getCurrentPeriod() {
        return ResponseEntity.ok(academicPeriodService.getCurrentPeriod());
    }


    @GetMapping("/date")
    public ResponseEntity<List<AcademicPeriodDto>> getPeriodsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(academicPeriodService.getPeriodsByDate(date));
    }


    @GetMapping("/date/type/{type}")
    public ResponseEntity<AcademicPeriodDto> getPeriodByDateAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String type) {
        return ResponseEntity.ok(academicPeriodService.getPeriodByDateAndType(date, type));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriodDto> createPeriod(@Valid @RequestBody AcademicPeriodDto periodDto) {
        return ResponseEntity.ok(academicPeriodService.createPeriod(periodDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriodDto> updatePeriod(
            @PathVariable Long id,
            @Valid @RequestBody AcademicPeriodDto periodDto) {
        return ResponseEntity.ok(academicPeriodService.updatePeriod(id, periodDto));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePeriod(@PathVariable Long id) {
        academicPeriodService.deletePeriod(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/set-current")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriodDto> setCurrentPeriod(@PathVariable Long id) {
        return ResponseEntity.ok(academicPeriodService.setCurrentPeriod(id));
    }
} 