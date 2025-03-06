package com.fivednevnik.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_relations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"parent_id", "child_id"})
})
public class UserRelation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;
    
    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private User child;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationType relationType;
    
    @Column(nullable = false)
    private boolean approved = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum RelationType {
        PARENT,
        GUARDIAN,
        GRAND_PARENT,
        SIBLING,
        OTHER
    }
} 