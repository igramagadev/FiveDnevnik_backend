package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDto {

    private Long id;
    
    @NotNull(message = "ID класса не может быть пустым")
    private Long classId;
    
    private String className;
    
    @NotNull(message = "ID предмета не может быть пустым")
    private Long subjectId;
    
    private String subjectName;
    
    private Long teacherId;
    
    private String teacherName;
    
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(max = 100, message = "Заголовок не должен превышать 100 символов")
    private String title;
    
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    
    @NotNull(message = "Срок выполнения не может быть пустым")
    @Future(message = "Срок выполнения должен быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Boolean isCompleted;
} 
