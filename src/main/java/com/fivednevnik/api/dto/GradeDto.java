package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
    private Integer gradeValue;

    public Integer getGradeValue() {
        return gradeValue != null ? gradeValue : 0;
    }
    
    @Builder.Default
    private Float weight = 1.0f;
    
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @Builder.Default
    private boolean isFinal = false;

    private String period;

    private String gradeType;

    @Builder.Default
    private String type = "REGULAR";

    private Long periodId;

    @Builder.Default
    private String periodType = "QUARTER";

    private Long classId;

    private Long academicPeriodId;

    private String academicPeriodName;


} 
