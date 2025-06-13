package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.GradeDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.GradeView;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.GradeViewRepository;
import com.fivednevnik.api.repository.SubjectRepository;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeViewService {
    
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final GradeViewRepository gradeViewRepository;

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentUsername(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        List<GradeView> grades = gradeViewRepository.findByStudentId(student.getId());
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentUsernameAndPeriod(String username, LocalDate from, LocalDate to) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);
        
        List<GradeView> grades = gradeViewRepository.findByStudentIdAndDateBetween(student.getId(), fromDateTime, toDateTime);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentId(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + studentId));
        
        List<GradeView> grades = gradeViewRepository.findByStudentId(studentId);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesBySubjectId(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
        
        List<GradeView> grades = gradeViewRepository.findBySubjectId(subjectId);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByClassName(String className) {
        List<GradeView> grades = gradeViewRepository.findByClassName(className);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByClassNameAndSubject(String className, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
                
        List<GradeView> grades = gradeViewRepository.findByClassNameAndSubjectId(className, subjectId);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private GradeDto mapToDto(GradeView grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .studentName(grade.getStudent() != null ? grade.getStudent().getFullName() : null)
                .subjectId(grade.getSubjectId())
                .subjectName(grade.getSubject() != null ? grade.getSubject().getName() : null)
                .teacherId(grade.getTeacherId())
                .teacherName(grade.getTeacher() != null ? grade.getTeacher().getFullName() : null)
                .gradeValue(grade.getGradeValue())
                .weight(grade.getWeight())
                .description(grade.getDescription())
                .date(grade.getDate())
                .isFinal(grade.isFinal())
                .period(grade.getPeriod())
                .gradeType(grade.getGradeType())
                .type(grade.getType())
                .periodId(grade.getPeriodId())
                .periodType(grade.getPeriodType())
                .classId(grade.getStudent() != null ? grade.getStudent().getClassId() : null)
                .build();
    }
} 