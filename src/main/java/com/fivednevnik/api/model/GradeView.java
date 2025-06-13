package com.fivednevnik.api.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades_view")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeView {

    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;
    
    @Column(name = "student_id")
    private Long studentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", insertable = false, updatable = false)
    private Subject subject;
    
    @Column(name = "subject_id")
    private Long subjectId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private User teacher;
    
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @Column(name = "\"grade_value\"", nullable = false)
    private Integer gradeValue;
    
    @Column(nullable = false)
    @Builder.Default
    private Float weight = 1.0f;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "is_final")
    @Builder.Default
    private boolean isFinal = false;

    private String period;

    @Column(name = "grade_type")
    @Builder.Default
    private String gradeType = "REGULAR";

    @Column(nullable = false)
    @Builder.Default
    private String type = "REGULAR";

    @Column(name = "period_id")
    @Builder.Default
    private Long periodId = 0L;

    @Column(name = "period_type")
    @Builder.Default
    private String periodType = "QUARTER";
} 