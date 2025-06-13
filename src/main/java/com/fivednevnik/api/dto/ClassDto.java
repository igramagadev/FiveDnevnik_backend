package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassDto {
    
    private Long id;
    private String name;
    private String academicYear;
    private Long classTeacherId;
    private String classTeacherName;
    private Integer studentCount;
} 
