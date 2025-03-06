package com.fivednevnik.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mass_mailings")
public class MassMailing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private GovernmentAgency agency;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetAudience targetAudience;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailingStatus status = MailingStatus.DRAFT;
    
    @Column(nullable = false)
    private boolean scheduled = false;
    
    private LocalDateTime scheduledTime;
    
    @OneToMany(mappedBy = "mailing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MailingAttachment> attachments = new ArrayList<>();
    
    @OneToMany(mappedBy = "mailing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MailingRecipient> recipients = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    private LocalDateTime sentAt;
    
    public enum TargetAudience {
        ALL_USERS,
        STUDENTS,
        PARENTS,
        TEACHERS,
        SCHOOL_DIRECTORS,
        CUSTOM
    }
    
    public enum MailingStatus {
        DRAFT,
        SCHEDULED,
        SENDING,
        SENT,
        FAILED,
        CANCELLED
    }
} 