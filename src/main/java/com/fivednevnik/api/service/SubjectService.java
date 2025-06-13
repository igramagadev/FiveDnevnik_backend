package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.SubjectDto;

import java.util.List;

public interface SubjectService {

    List<SubjectDto> getAllSubjects();

    SubjectDto getSubjectById(Long id);

    SubjectDto createSubject(SubjectDto subjectDto);

    SubjectDto updateSubject(Long id, SubjectDto subjectDto);

    void deleteSubject(Long id);
    
} 
