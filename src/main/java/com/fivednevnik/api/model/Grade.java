package com.fivednevnik.api.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "grades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;
    
    @Column(name = "grade_value", nullable = true)
    @Builder.Default
    private Integer gradeValue = 0;

    
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

    @Column(name = "type_compat", nullable = false)
    @Builder.Default
    private String type = "REGULAR";

    @Column(name = "period_id")
    @Builder.Default
    private Long periodId = 0L;

    @Column(name = "period_type")
    private String periodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_period_id")
    private AcademicPeriod academicPeriod;

    @PrePersist
    @PreUpdate
    public void syncValues() {
        if (gradeValue == null) gradeValue = 0;
    }

} 
