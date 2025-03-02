package com.fivednevnik.backend.dto;

import com.fivednevnik.backend.model.GradeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private Long subjectId;
    private String subjectName;
    private Long lessonId;
    private Integer value;
    private String comment;
    private LocalDate date;
    private GradeType gradeType;
} 