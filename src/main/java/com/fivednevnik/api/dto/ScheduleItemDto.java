package com.fivednevnik.api.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemDto {

    private Long id;
    
    @NotNull(message = "ID класса не может быть пустым")
    private Long classId;
    
    private String className;
    
    @NotNull(message = "ID предмета не может быть пустым")
    private Long subjectId;
    
    private String subjectName;
    
    @NotNull(message = "ID учителя не может быть пустым")
    private Long teacherId;
    
    private String teacherName;
    
    @NotNull(message = "День недели не может быть пустым")
    @Min(value = 1, message = "День недели должен быть от 1 до 7")
    @Max(value = 7, message = "День недели должен быть от 1 до 7")
    private Integer dayOfWeek;
    
    @NotNull(message = "Номер урока не может быть пустым")
    @Min(value = 1, message = "Номер урока должен быть положительным числом")
    private Integer lessonNumber;
    
    @NotBlank(message = "Время начала не может быть пустым")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Время начала должно быть в формате HH:MM")
    private String startTime;
    
    @NotBlank(message = "Время окончания не может быть пустым")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Время окончания должно быть в формате HH:MM")
    private String endTime;
    
    private String classroom;
} 
