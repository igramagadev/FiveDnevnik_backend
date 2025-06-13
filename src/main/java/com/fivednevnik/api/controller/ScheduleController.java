package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ScheduleDto;
import com.fivednevnik.api.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping("/class/{className}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByClassName(@PathVariable String className) {
        return ResponseEntity.ok(scheduleService.getSchedulesByClassName(className));
    }

    @GetMapping("/class/{className}/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByClassNameAndDayOfWeek(
            @PathVariable String className,
            @PathVariable Integer dayOfWeek) {
        return ResponseEntity.ok(scheduleService.getSchedulesByClassNameAndDayOfWeek(className, dayOfWeek));
    }


    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByTeacherId(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByTeacherId(teacherId));
    }

    @GetMapping("/teacher/{teacherId}/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByTeacherIdAndDayOfWeek(
            @PathVariable Long teacherId,
            @PathVariable Integer dayOfWeek) {
        return ResponseEntity.ok(scheduleService.getSchedulesByTeacherIdAndDayOfWeek(teacherId, dayOfWeek));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByStudentId(studentId));
    }


    @GetMapping("/student/{studentId}/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByStudentIdAndDayOfWeek(
            @PathVariable Long studentId,
            @PathVariable Integer dayOfWeek) {
        return ResponseEntity.ok(scheduleService.getSchedulesByStudentIdAndDayOfWeek(studentId, dayOfWeek));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(scheduleService.getSchedulesBySubjectId(subjectId));
    }

    @GetMapping("/class/{className}/subject/{subjectId}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByClassNameAndSubjectId(
            @PathVariable String className,
            @PathVariable Long subjectId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByClassNameAndSubjectId(className, subjectId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDto> createSchedule(@Valid @RequestBody ScheduleDto scheduleDto) {

        return ResponseEntity.ok(scheduleService.createSchedule(scheduleDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDto> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleDto scheduleDto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDto> activateSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.toggleScheduleActive(id, true));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDto> deactivateSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.toggleScheduleActive(id, false));
    }
} 
