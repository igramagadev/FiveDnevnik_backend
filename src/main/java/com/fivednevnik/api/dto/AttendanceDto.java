package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long subjectId;

    private String subjectName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private boolean isPresent;

    private String reason;

    private boolean isExcused;

    private Long teacherId;

    private String teacherName;

    private Integer lessonNumber;

    private String period;

    private Long academicPeriodId;

    private String academicPeriodName;

    private Long classId;

    private String className;
} 
