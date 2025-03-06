package com.fivednevnik.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "analytical_reports")
public class AnalyticalReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String queryDefinition; // JSON определение запроса
    
    @Column(columnDefinition = "TEXT")
    private String resultCache; // Кэшированные результаты в JSON
    
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    
    @Column(nullable = false)
    private boolean isPublic = false;
    
    @Column(nullable = false)
    private boolean scheduled = false;
    
    private String scheduleExpression; // Cron-выражение для расписания
    
    @Column(nullable = false)
    private LocalDateTime lastRun;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum ReportType {
        ACADEMIC_PERFORMANCE,
        ATTENDANCE,
        TEACHER_WORKLOAD,
        SCHOOL_COMPARISON,
        CUSTOM
    }
} 