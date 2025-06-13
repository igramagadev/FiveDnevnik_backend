package com.fivednevnik.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    
    private Long id;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherName;
    private Integer dayOfWeek;
    private String dayOfWeekName;
    private Integer lessonNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private String classroom;
    private boolean isActive;
} 