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
@Table(name = "monitoring_responses")
public class MonitoringResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    private MonitoringForm form;
    
    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    @ManyToOne
    @JoinColumn(name = "submitter_id", nullable = false)
    private User submitter;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseData; // JSON данные ответа
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResponseStatus status = ResponseStatus.DRAFT;
    
    @Column(columnDefinition = "TEXT")
    private String reviewComments;
    
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum ResponseStatus {
        DRAFT,
        SUBMITTED,
        UNDER_REVIEW,
        NEEDS_REVISION,
        APPROVED,
        REJECTED
    }
} 