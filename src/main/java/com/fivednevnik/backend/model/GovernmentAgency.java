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
@Table(name = "government_agencies")
public class GovernmentAgency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgencyType agencyType;
    
    private String description;
    
    private String address;
    
    private String phone;
    
    private String email;
    
    private String website;
    
    @ManyToOne
    @JoinColumn(name = "head_id")
    private User head;
    
    @OneToMany(mappedBy = "agency")
    private List<School> supervisedSchools = new ArrayList<>();
    
    @OneToMany(mappedBy = "parentAgency")
    private List<GovernmentAgency> childAgencies = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "parent_agency_id")
    private GovernmentAgency parentAgency;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum AgencyType {
        MINISTRY,
        DEPARTMENT,
        MUNICIPALITY,
        DISTRICT,
        OTHER
    }
} 