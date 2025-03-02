package com.fivednevnik.backend.service;

import com.fivednevnik.backend.dto.GradeDto;
import com.fivednevnik.backend.model.Grade;
import com.fivednevnik.backend.model.Lesson;
import com.fivednevnik.backend.model.Subject;
import com.fivednevnik.backend.model.User;
import com.fivednevnik.backend.repository.GradeRepository;
import com.fivednevnik.backend.repository.LessonRepository;
import com.fivednevnik.backend.repository.SubjectRepository;
import com.fivednevnik.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private LessonRepository lessonRepository;

    public List<GradeDto> getGradesByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Ученик не найден с ID: " + studentId));
        
        return gradeRepository.findByStudent(student).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<GradeDto> getGradesByStudentAndSubject(Long studentId, Long subjectId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Ученик не найден с ID: " + studentId));
        
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Предмет не найден с ID: " + subjectId));
        
        return gradeRepository.findByStudentAndSubject(student, subject).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<GradeDto> getGradesByStudentAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Ученик не найден с ID: " + studentId));
        
        return gradeRepository.findByStudentAndDateBetween(student, startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public GradeDto createGrade(GradeDto gradeDto) {
        User student = userRepository.findById(gradeDto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Ученик не найден с ID: " + gradeDto.getStudentId()));
        
        User teacher = userRepository.findById(gradeDto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Учитель не найден с ID: " + gradeDto.getTeacherId()));
        
        Subject subject = subjectRepository.findById(gradeDto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден с ID: " + gradeDto.getSubjectId()));
        
        Lesson lesson = null;
        if (gradeDto.getLessonId() != null) {
            lesson = lessonRepository.findById(gradeDto.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Урок не найден с ID: " + gradeDto.getLessonId()));
        }
        
        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setSubject(subject);
        grade.setLesson(lesson);
        grade.setValue(gradeDto.getValue());
        grade.setComment(gradeDto.getComment());
        grade.setDate(gradeDto.getDate() != null ? gradeDto.getDate() : LocalDate.now());
        grade.setGradeType(gradeDto.getGradeType());
        
        Grade savedGrade = gradeRepository.save(grade);
        return convertToDto(savedGrade);
    }

    public GradeDto updateGrade(Long id, GradeDto gradeDto) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оценка не найдена с ID: " + id));
        
        grade.setValue(gradeDto.getValue());
        grade.setComment(gradeDto.getComment());
        grade.setGradeType(gradeDto.getGradeType());
        
        Grade updatedGrade = gradeRepository.save(grade);
        return convertToDto(updatedGrade);
    }

    public void deleteGrade(Long id) {
        if (!gradeRepository.existsById(id)) {
            throw new RuntimeException("Оценка не найдена с ID: " + id);
        }
        gradeRepository.deleteById(id);
    }

    private GradeDto convertToDto(Grade grade) {
        return new GradeDto(
                grade.getId(),
                grade.getStudent().getId(),
                grade.getStudent().getFullName(),
                grade.getTeacher().getId(),
                grade.getTeacher().getFullName(),
                grade.getSubject().getId(),
                grade.getSubject().getName(),
                grade.getLesson() != null ? grade.getLesson().getId() : null,
                grade.getValue(),
                grade.getComment(),
                grade.getDate(),
                grade.getGradeType()
        );
    }
} 