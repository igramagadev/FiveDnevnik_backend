package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ScheduleItemDto;
import com.fivednevnik.api.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с расписанием занятий
 */
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Получение расписания для текущего пользователя
     */
    @GetMapping("/my")
    public ResponseEntity<List<ScheduleItemDto>> getMySchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Запрос расписания для пользователя: {}", username);
        return ResponseEntity.ok(scheduleService.getScheduleForUser(username));
    }

    /**
     * Получение расписания для класса
     */
    @GetMapping("/class/{className}")
    public ResponseEntity<List<ScheduleItemDto>> getScheduleByClass(@PathVariable String className) {
        log.info("Запрос расписания для класса: {}", className);
        return ResponseEntity.ok(scheduleService.getScheduleByClassName(className));
    }
    
    /**
     * Получение расписания по ID учителя
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ScheduleItemDto>> getScheduleByTeacher(@PathVariable Long teacherId) {
        log.info("Запрос расписания для учителя с ID: {}", teacherId);
        return ResponseEntity.ok(scheduleService.getScheduleByTeacherId(teacherId));
    }
    
    /**
     * Получение расписания по дню недели
     */
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleItemDto>> getScheduleByDay(
            @PathVariable Integer dayOfWeek,
            @RequestParam(required = false) String className) {
        log.info("Запрос расписания на день {} для класса {}", dayOfWeek, className);
        return ResponseEntity.ok(scheduleService.getScheduleByDayAndClass(dayOfWeek, className));
    }

    /**
     * Создание нового элемента расписания (только для администраторов)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleItemDto> createScheduleItem(
            @Valid @RequestBody ScheduleItemDto scheduleItemDto) {
        log.info("Создание нового элемента расписания: {}", scheduleItemDto);
        return ResponseEntity.ok(scheduleService.createScheduleItem(scheduleItemDto));
    }

    /**
     * Обновление элемента расписания (только для администраторов)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleItemDto> updateScheduleItem(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleItemDto scheduleItemDto) {
        log.info("Обновление элемента расписания с ID: {}", id);
        return ResponseEntity.ok(scheduleService.updateScheduleItem(id, scheduleItemDto));
    }

    /**
     * Удаление элемента расписания (только для администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteScheduleItem(@PathVariable Long id) {
        log.info("Удаление элемента расписания с ID: {}", id);
        scheduleService.deleteScheduleItem(id);
        return ResponseEntity.noContent().build();
    }
} 