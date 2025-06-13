package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.GradeDto;
import com.fivednevnik.api.exception.AccessDeniedException;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.AcademicPeriod;
import com.fivednevnik.api.model.Grade;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.AcademicPeriodRepository;
import com.fivednevnik.api.repository.GradeRepository;
import com.fivednevnik.api.repository.SubjectRepository;
import com.fivednevnik.api.repository.UserRepository;
import com.fivednevnik.api.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor

public class GradeService {

    
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final WebSocketService webSocketService;
    private final AcademicPeriodRepository academicPeriodRepository;
    

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentUsername(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        List<Grade> grades = gradeRepository.findByStudent(student);
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
        
        List<Grade> grades = gradeRepository.findByStudentAndDateBetween(student, fromDateTime, toDateTime);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentId(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + studentId));
        
        List<Grade> grades = gradeRepository.findByStudent(student);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesBySubjectId(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
        
        List<Grade> grades = gradeRepository.findBySubject(subject);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByClassName(String className) {
        List<Grade> grades = gradeRepository.findByClassName(className);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByClassNameAndSubject(String className, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
                
        List<Grade> grades = gradeRepository.findByClassNameAndSubject(className, subject);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesBySubjectAndPeriod(Long subjectId, String period) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
        List<Grade> grades = gradeRepository.findBySubject(subject).stream()
                .filter(g -> g.getPeriod().equals(period))
                .collect(Collectors.toList());
                
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getFinalGradesByStudentAndPeriod(Long studentId, String period) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + studentId));
        
        List<Grade> grades = gradeRepository.findByStudentAndIsFinalTrueAndPeriod(student, period);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeDto createGrade(GradeDto gradeDto, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));
        
        User student = userRepository.findById(gradeDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + gradeDto.getStudentId()));
        
        Subject subject = subjectRepository.findById(gradeDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + gradeDto.getSubjectId()));

        Integer gradeValue = gradeDto.getGradeValue();
        if (gradeValue == null) {
            gradeValue = 0;
        }
        
        Grade grade = Grade.builder()
                .student(student)
                .subject(subject)
                .teacher(teacher)
                .gradeValue(gradeValue)
                .weight(gradeDto.getWeight() != null ? gradeDto.getWeight() : 1.0f)
                .description(gradeDto.getDescription())
                .date(gradeDto.getDate() != null ? gradeDto.getDate() : LocalDateTime.now())
                .isFinal(gradeDto.isFinal())
                .period(gradeDto.getPeriod())
                .gradeType(gradeDto.getGradeType() != null ? gradeDto.getGradeType() : "REGULAR")
                .type(gradeDto.getType() != null ? gradeDto.getType() : "REGULAR")
                .periodId(gradeDto.getPeriodId() != null ? gradeDto.getPeriodId() : 0L)
                .periodType(gradeDto.getPeriodType() != null ? gradeDto.getPeriodType() : "QUARTER")
                .build();

        if (grade.getGradeValue() == null) {
            grade.setGradeValue(0);
        }
        if (grade.getGradeValue() == null) {
            grade.setGradeValue(grade.getGradeValue());
        }
        
        try {
            Grade savedGrade = gradeRepository.saveAndFlush(grade);
            
            GradeDto savedGradeDto = mapToDto(savedGrade);

            try {
                webSocketService.notifyGradeCreated(savedGradeDto);
            } catch (Exception e) {
            }

            return savedGradeDto;
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public GradeDto createGradeWithSync(GradeDto gradeDto, String teacherUsername) {
        GradeDto savedGradeDto = createGrade(gradeDto, teacherUsername);

        Grade checkGrade = gradeRepository.findById(savedGradeDto.getId())
                .orElse(null);
                
        if (checkGrade == null) {
            throw new RuntimeException("Ошибка при сохранении оценки: оценка не найдена после сохранения");
        }

        return savedGradeDto;
    }

    @Transactional(readOnly = true)
    public List<GradeDto> forceSyncGradesFromServer(String username) {
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        List<Grade> grades;

        if (user.getRole().equals("ROLE_STUDENT")) {
            grades = gradeRepository.findByStudent(user);
        } else if (user.getRole().equals("ROLE_TEACHER") || user.getRole().equals("ROLE_CLASS_TEACHER")) {
            grades = gradeRepository.findByTeacher(user);
        } else {
            grades = gradeRepository.findAll();
        }
        
        List<GradeDto> gradeDtos = grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return gradeDtos;
    }

    @Transactional
    public List<GradeDto> createBatchGrades(List<GradeDto> gradeDtos, String teacherUsername) {
        
        List<GradeDto> createdGrades = new ArrayList<>();
        for (GradeDto gradeDto : gradeDtos) {
            GradeDto createdGrade = createGrade(gradeDto, teacherUsername);
            createdGrades.add(createdGrade);
        }
        
        return createdGrades;
    }
    

    @Transactional
    public GradeDto updateGrade(Long id, GradeDto gradeDto, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));
        
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Оценка не найдена с ID: " + id));

        if (!grade.getTeacher().getId().equals(teacher.getId()) && !teacher.getRole().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("У вас нет прав на редактирование этой оценки");
        }
        
        if (gradeDto.getStudentId() != null) {
            User student = userRepository.findById(gradeDto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + gradeDto.getStudentId()));
            grade.setStudent(student);
        }
        
        if (gradeDto.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(gradeDto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + gradeDto.getSubjectId()));
            grade.setSubject(subject);
        }

        if (gradeDto.getGradeValue() != null) {
            grade.setGradeValue(gradeDto.getGradeValue());
            grade.setGradeValue(gradeDto.getGradeValue());
        }
        
        if (gradeDto.getWeight() != null) {
            grade.setWeight(gradeDto.getWeight());
        }
        
        if (gradeDto.getDescription() != null) {
            grade.setDescription(gradeDto.getDescription());
        }
        
        if (gradeDto.getDate() != null) {
            grade.setDate(gradeDto.getDate());
        }
        
        if (gradeDto.getPeriod() != null) {
            grade.setPeriod(gradeDto.getPeriod());
        }

        if (gradeDto.getGradeType() != null) {
            grade.setGradeType(gradeDto.getGradeType());
        }
        
        if (gradeDto.getType() != null) {
            grade.setType(gradeDto.getType());
        }
        
        if (gradeDto.getPeriodId() != null) {
            grade.setPeriodId(gradeDto.getPeriodId());
        }
        
        if (gradeDto.getPeriodType() != null) {
            grade.setPeriodType(gradeDto.getPeriodType());
        }
        
        grade.setFinal(gradeDto.isFinal());

        if (grade.getGradeValue() == null) {
            grade.setGradeValue(0);
        }
        if (grade.getGradeValue() == null) {
            grade.setGradeValue(grade.getGradeValue());
        }
        
        Grade updatedGrade = gradeRepository.save(grade);
        
        GradeDto updatedGradeDto = mapToDto(updatedGrade);

        webSocketService.notifyGradeUpdated(updatedGradeDto);
        
        return updatedGradeDto;
    }

    @Transactional
    public void deleteGrade(Long id, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));
        
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Оценка не найдена с ID: " + id));

        if (!grade.getTeacher().getId().equals(teacher.getId()) && !teacher.getRole().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("У вас нет прав на удаление этой оценки");
        }

        GradeDto gradeDto = mapToDto(grade);

        gradeRepository.delete(grade);

        webSocketService.notifyGradeDeleted(gradeDto);
    }

    public GradeDto mapToDto(Grade grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .studentId(grade.getStudent().getId())
                .studentName(grade.getStudent().getLastName() + " " + grade.getStudent().getFirstName())
                .subjectId(grade.getSubject().getId())
                .subjectName(grade.getSubject().getName())
                .teacherId(grade.getTeacher().getId())
                .teacherName(grade.getTeacher().getLastName() + " " + grade.getTeacher().getFirstName())
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
                .build();
    }

    public User getStudentByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentAndSubjectAndPeriod(Long studentId, Long subjectId, String period) {
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + studentId));
        
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
        
        List<Grade> grades = gradeRepository.findByStudentAndSubjectAndPeriod(student, subject, period);
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByStudentAndPeriod(Long studentId, String period) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + studentId));

        List<Grade> grades = gradeRepository.findAll().stream()
                .filter(g -> g.getStudent().getId().equals(studentId) && g.getPeriod().equals(period))
                .collect(Collectors.toList());
                
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getGradesByClassNameAndPeriod(String className, String period) {
        List<Grade> grades = gradeRepository.findAll().stream()
                .filter(g -> g.getStudent().getClassName().equals(className) && g.getPeriod().equals(period))
                .collect(Collectors.toList());
                
        return grades.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeDto createGradeWithDate(GradeDto gradeDto, String teacherUsername, LocalDate date) {
        LocalDateTime dateTime = date.atStartOfDay();
        gradeDto.setDate(dateTime);
        try {
            AcademicPeriod academicPeriod = academicPeriodRepository.findByDateAndType(date, "QUARTER")
                    .orElse(null);
            
            if (academicPeriod != null) {
                gradeDto.setAcademicPeriodId(academicPeriod.getId());
                gradeDto.setAcademicPeriodName(academicPeriod.getName());
                gradeDto.setPeriod(academicPeriod.getName());
            }
        } catch (Exception e) {
        }

        return createGrade(gradeDto, teacherUsername);
    }
} 
