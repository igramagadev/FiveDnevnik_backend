package com.fivednevnik.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для предмета
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    
} 