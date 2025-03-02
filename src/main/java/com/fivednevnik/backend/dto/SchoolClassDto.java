package com.fivednevnik.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassDto {
    private Long id;
    private String name;
    private Integer grade;
    private Long classTeacherId;
    private String classTeacherName;
    private List<UserDto> students;
} 