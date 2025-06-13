package com.fivednevnik.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeTableDto {

    private String subjectName;

    private Long subjectId;

    private List<LocalDate> dates;

    private List<StudentGradesRow> students;

    private Map<LocalDate, Float> averageGrades;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentGradesRow {
        private Long studentId;

        private String studentName;

        private Map<LocalDate, List<GradeDto>> gradesByDate;

        private Float averageGrade;

        private Integer finalGrade;
    }
} 