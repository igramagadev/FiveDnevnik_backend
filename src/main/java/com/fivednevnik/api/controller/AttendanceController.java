package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.AttendanceDto;
import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.service.AttendanceService;
import com.fivednevnik.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<AttendanceDto>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) Long subjectId) {
        return ResponseEntity.ok(attendanceService.getAllAttendance(startDate, endDate, subjectId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN', 'STUDENT', 'PARENT')")
    public ResponseEntity<AttendanceDto> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN', 'STUDENT', 'PARENT')")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentId(studentId));
    }


    @GetMapping("/class/{className}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByClassName(@PathVariable String className) {
        return ResponseEntity.ok(attendanceService.getAttendanceByClassName(className));
    }

    @GetMapping("/student/{studentId}/date")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN', 'STUDENT', 'PARENT')")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByStudentIdAndDate(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentIdAndDate(studentId, date));
    }

    @GetMapping("/class/{className}/date")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByClassNameAndDate(
            @PathVariable String className,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByClassNameAndDate(className, date));
    }

    @GetMapping("/student/{studentId}/date-range")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN', 'STUDENT', 'PARENT')")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByStudentIdAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudentIdAndDateRange(studentId, fromDate, toDate));
    }

    @GetMapping("/class/{className}/date-range")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByClassNameAndDateRange(
            @PathVariable String className,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceByClassNameAndDateRange(className, fromDate, toDate));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<AttendanceDto> createAttendance(@Valid @RequestBody AttendanceDto attendanceDto) {
        return ResponseEntity.ok(attendanceService.createAttendance(attendanceDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<AttendanceDto> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDto attendanceDto) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, attendanceDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAttendanceStats(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) Long subjectId) {
        return ResponseEntity.ok(attendanceService.getAttendanceStats(userId, periodId, subjectId));
    }
} 
