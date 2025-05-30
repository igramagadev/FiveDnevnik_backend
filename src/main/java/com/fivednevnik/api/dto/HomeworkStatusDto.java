package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных о статусе выполнения домашнего задания
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkStatusDto {

    private Long id;
    
    private Long homeworkId;
    
    private Long studentId;
    
    private String studentName;
    
    private Boolean isCompleted;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    private String comment;
} 