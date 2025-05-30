package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ClassDto;
import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.service.ClassService;
import com.fivednevnik.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с классами
 */
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Slf4j
public class ClassController {

    private final ClassService classService;
    private final UserService userService;

    /**
     * Получение списка всех классов
     */
    @GetMapping
    public ResponseEntity<?> getAllClasses() {
        log.debug("Запрос на получение всех классов");
        
        try {
            List<ClassDto> classes = classService.getAllClasses();
            log.info("Получен список классов, количество: {}", classes.size());
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            log.error("Ошибка при получении списка классов", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка классов: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Получение информации о конкретном классе
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        log.debug("Запрос на получение класса с ID: {}", id);
        
        try {
            ClassDto classDto = classService.getClassById(id);
            log.info("Получена информация о классе: {}", classDto.getName());
            return ResponseEntity.ok(classDto);
        } catch (Exception e) {
            log.error("Ошибка при получении информации о классе с ID: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении информации о классе: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Получение списка учеников класса
     */
    @GetMapping("/{id}/students")
    public ResponseEntity<?> getClassStudents(@PathVariable Long id) {
        log.debug("Запрос на получение учеников класса с ID: {}", id);
        
        try {
            List<UserDto> students = userService.findUsersByRoleAndClassId(User.Role.STUDENT, id);
            log.info("Получен список учеников класса, количество: {}", students.size());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            log.error("Ошибка при получении списка учеников класса с ID: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка учеников: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Создание нового класса (только для администраторов)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClass(@RequestBody ClassDto classDto) {
        log.debug("Запрос на создание нового класса: {}", classDto.getName());
        
        try {
            ClassDto createdClass = classService.createClass(classDto);
            log.info("Создан новый класс: {}", createdClass.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
        } catch (Exception e) {
            log.error("Ошибка при создании нового класса", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при создании класса: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Обновление информации о классе (только для администраторов)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateClass(@PathVariable Long id, @RequestBody ClassDto classDto) {
        log.debug("Запрос на обновление класса с ID: {}", id);
        
        try {
            ClassDto updatedClass = classService.updateClass(id, classDto);
            log.info("Обновлен класс: {}", updatedClass.getName());
            return ResponseEntity.ok(updatedClass);
        } catch (Exception e) {
            log.error("Ошибка при обновлении класса с ID: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при обновлении класса: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Удаление класса (только для администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        log.debug("Запрос на удаление класса с ID: {}", id);
        
        try {
            classService.deleteClass(id);
            log.info("Удален класс с ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ошибка при удалении класса с ID: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при удалении класса: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
} 