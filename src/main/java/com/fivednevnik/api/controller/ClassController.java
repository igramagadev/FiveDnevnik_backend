package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ClassDto;
import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.service.ClassService;
import com.fivednevnik.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllClasses() {
        
        try {
            List<ClassDto> classes = classService.getAllClasses();
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка классов: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {

        try {
            ClassDto classDto = classService.getClassById(id);
            return ResponseEntity.ok(classDto);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении информации о классе: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<?> getClassStudents(@PathVariable Long id) {

        try {
            List<UserDto> students = userService.findUsersByRoleAndClassId(User.Role.STUDENT, id);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка учеников: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClass(@RequestBody ClassDto classDto) {
        
        try {
            ClassDto createdClass = classService.createClass(classDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при создании класса: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateClass(@PathVariable Long id, @RequestBody ClassDto classDto) {

        try {
            ClassDto updatedClass = classService.updateClass(id, classDto);
            return ResponseEntity.ok(updatedClass);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при обновлении класса: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        
        try {
            classService.deleteClass(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при удалении класса: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('TEACHER', 'CLASS_TEACHER', 'ADMIN')")
    public ResponseEntity<?> getStudentsByClassName(@RequestParam String className) {

        try {
            List<UserDto> students = userService.findUsersByRoleAndClassName(User.Role.STUDENT, className);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка учеников: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
} 
