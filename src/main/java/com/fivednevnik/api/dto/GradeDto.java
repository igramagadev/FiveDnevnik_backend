package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных об оценках
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDto {

    private Long id;
    
    @NotNull(message = "ID ученика не может быть пустым")
    private Long studentId;
    
    private String studentName;
    
    @NotNull(message = "ID предмета не может быть пустым")
    private Long subjectId;
    
    private String subjectName;
    
    private Long teacherId;
    
    private String teacherName;
    
    @NotNull(message = "Значение оценки не может быть пустым")
    @Min(value = 1, message = "Минимальное значение оценки: 1")
    @Max(value = 5, message = "Максимальное значение оценки: 5")
    private Integer value;
    
    @Builder.Default
    private Float weight = 1.0f;
    
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
} 