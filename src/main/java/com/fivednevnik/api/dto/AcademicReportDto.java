package com.fivednevnik.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicReportDto {

    private String reportName;

    private String periodName;

    private Long periodId;

    private String className;

    private ClassStatistics classStatistics;

    private List<SubjectStatistics> subjectStatistics;

    private List<StudentStatistics> studentStatistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassStatistics {
        private Float averageGrade;

        private Integer excellentStudentsCount;

        private Integer goodStudentsCount;

        private Integer averageStudentsCount;

        private Integer underperformingStudentsCount;

        private Float performancePercentage;

        private Float qualityPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectStatistics {
        private Long subjectId;

        private String subjectName;

        private Float averageGrade;

        private Integer excellentCount;

        private Integer goodCount;

        private Integer averageCount;

        private Integer poorCount;

        private Float performancePercentage;

        private Float qualityPercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentStatistics {

        private Long studentId;

        private String studentName;

        private Float averageGrade;

        private Map<String, Float> subjectAverages;

        private Map<String, Integer> finalGrades;

        private Integer absencesCount;

        private Integer classRank;
    }
} 