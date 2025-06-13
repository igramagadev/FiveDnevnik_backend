package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.SubjectDto;
import com.fivednevnik.api.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor

public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<?> getAllSubjects() {
        
        try {
            List<SubjectDto> subjects = subjectService.getAllSubjects();
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка предметов: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable Long id) {
        
        try {
            SubjectDto subjectDto = subjectService.getSubjectById(id);
            return ResponseEntity.ok(subjectDto);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении информации о предмете: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSubject(@RequestBody SubjectDto subjectDto) {
        
        try {
            SubjectDto createdSubject = subjectService.createSubject(subjectDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при создании предмета: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody SubjectDto subjectDto) {
        
        try {
            SubjectDto updatedSubject = subjectService.updateSubject(id, subjectDto);
            return ResponseEntity.ok(updatedSubject);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при обновлении предмета: " + e.getMessage(), "SERVER_ERROR"));
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при удалении предмета: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
} 
