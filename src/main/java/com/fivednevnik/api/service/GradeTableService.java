package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.GradeDto;
import com.fivednevnik.api.dto.GradeTableDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.AcademicPeriod;
import com.fivednevnik.api.model.Grade;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.AcademicPeriodRepository;
import com.fivednevnik.api.repository.GradeRepository;
import com.fivednevnik.api.repository.SubjectRepository;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeTableService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final GradeService gradeService;

    @Transactional(readOnly = true)
    public GradeTableDto getGradeTableByClassAndSubjectAndPeriod(String className, Long subjectId, String period) {

        
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));

        List<User> students = userRepository.findByRoleAndClassName(User.Role.STUDENT, className);
        
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("Ученики не найдены для класса: " + className);
        }

        List<Grade> allGrades = new ArrayList<>();
        for (User student : students) {
            List<Grade> studentGrades = gradeRepository.findByStudentAndSubjectAndPeriod(student, subject, period);
            allGrades.addAll(studentGrades);
        }

        Set<LocalDate> uniqueDatesSet = allGrades.stream()
                .map(grade -> grade.getDate().toLocalDate())
                .collect(Collectors.toSet());

        List<LocalDate> dates = new ArrayList<>(uniqueDatesSet);
        Collections.sort(dates);

        List<GradeTableDto.StudentGradesRow> studentRows = new ArrayList<>();
        Map<LocalDate, List<Integer>> gradesByDate = new HashMap<>();
        
        for (User student : students) {
            List<Grade> studentGrades = allGrades.stream()
                    .filter(grade -> grade.getStudent().getId().equals(student.getId()))
                    .collect(Collectors.toList());

            Map<LocalDate, List<GradeDto>> gradesByDateForStudent = new HashMap<>();
            for (LocalDate date : dates) {
                List<Grade> gradesForDate = studentGrades.stream()
                        .filter(grade -> grade.getDate().toLocalDate().equals(date))
                        .collect(Collectors.toList());
                
                List<GradeDto> gradeDtos = gradesForDate.stream()
                        .map(gradeService::mapToDto)
                        .collect(Collectors.toList());
                
                gradesByDateForStudent.put(date, gradeDtos);

                List<Integer> gradesForDateList = gradesByDate.getOrDefault(date, new ArrayList<>());
                gradesForDate.forEach(grade -> gradesForDateList.add(grade.getGradeValue()));
                gradesByDate.put(date, gradesForDateList);
            }

            float averageGrade = calculateAverageGrade(studentGrades);

            Integer finalGrade = getFinalGrade(studentGrades);

            GradeTableDto.StudentGradesRow row = GradeTableDto.StudentGradesRow.builder()
                    .studentId(student.getId())
                    .studentName(student.getLastName() + " " + student.getFirstName())
                    .gradesByDate(gradesByDateForStudent)
                    .averageGrade(averageGrade)
                    .finalGrade(finalGrade)
                    .build();
            
            studentRows.add(row);
        }

        Map<LocalDate, Float> averageGrades = new HashMap<>();
        for (Map.Entry<LocalDate, List<Integer>> entry : gradesByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Integer> grades = entry.getValue();
            
            if (!grades.isEmpty()) {
                float sum = 0;
                for (Integer grade : grades) {
                    sum += grade;
                }
                float average = sum / grades.size();
                averageGrades.put(date, average);
            }
        }

        return GradeTableDto.builder()
                .subjectName(subject.getName())
                .subjectId(subject.getId())
                .dates(dates)
                .students(studentRows)
                .averageGrades(averageGrades)
                .build();
    }

    @Transactional(readOnly = true)
    public GradeTableDto getGradeTableByClassAndSubjectAndAcademicPeriod(
            String className, Long subjectId, Long academicPeriodId) {
        
        AcademicPeriod academicPeriod = academicPeriodRepository.findById(academicPeriodId)
                .orElseThrow(() -> new ResourceNotFoundException("Академический период не найден с ID: " + academicPeriodId));

        return getGradeTableByClassAndSubjectAndPeriod(className, subjectId, academicPeriod.getName());
    }

    @Transactional(readOnly = true)
    public GradeTableDto getGradeTableByClassAndSubjectAndDateRange(
            String className, Long subjectId, LocalDate startDate, LocalDate endDate) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));

        List<User> students = userRepository.findByRoleAndClassName(User.Role.STUDENT, className);
        
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("Ученики не найдены для класса: " + className);
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<Grade> allGrades = new ArrayList<>();
        for (User student : students) {
            List<Grade> studentGrades = gradeRepository.findByStudentAndSubjectAndDateBetween(
                    student, subject, startDateTime, endDateTime);
            allGrades.addAll(studentGrades);
        }

        Set<LocalDate> uniqueDatesSet = allGrades.stream()
                .map(grade -> grade.getDate().toLocalDate())
                .collect(Collectors.toSet());

        List<LocalDate> dates = new ArrayList<>(uniqueDatesSet);
        Collections.sort(dates);

        List<GradeTableDto.StudentGradesRow> studentRows = new ArrayList<>();
        Map<LocalDate, List<Integer>> gradesByDate = new HashMap<>();
        
        for (User student : students) {
            List<Grade> studentGrades = allGrades.stream()
                    .filter(grade -> grade.getStudent().getId().equals(student.getId()))
                    .collect(Collectors.toList());

            Map<LocalDate, List<GradeDto>> gradesByDateForStudent = new HashMap<>();
            for (LocalDate date : dates) {
                List<Grade> gradesForDate = studentGrades.stream()
                        .filter(grade -> grade.getDate().toLocalDate().equals(date))
                        .collect(Collectors.toList());
                
                List<GradeDto> gradeDtos = gradesForDate.stream()
                        .map(gradeService::mapToDto)
                        .collect(Collectors.toList());
                
                gradesByDateForStudent.put(date, gradeDtos);

                List<Integer> gradesForDateList = gradesByDate.getOrDefault(date, new ArrayList<>());
                gradesForDate.forEach(grade -> gradesForDateList.add(grade.getGradeValue()));
                gradesByDate.put(date, gradesForDateList);
            }

            float averageGrade = calculateAverageGrade(studentGrades);

            GradeTableDto.StudentGradesRow row = GradeTableDto.StudentGradesRow.builder()
                    .studentId(student.getId())
                    .studentName(student.getLastName() + " " + student.getFirstName())
                    .gradesByDate(gradesByDateForStudent)
                    .averageGrade(averageGrade)
                    .finalGrade(null)
                    .build();
            
            studentRows.add(row);
        }

        Map<LocalDate, Float> averageGrades = new HashMap<>();
        for (Map.Entry<LocalDate, List<Integer>> entry : gradesByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Integer> grades = entry.getValue();
            
            if (!grades.isEmpty()) {
                float sum = 0;
                for (Integer grade : grades) {
                    sum += grade;
                }
                float average = sum / grades.size();
                averageGrades.put(date, average);
            }
        }

        return GradeTableDto.builder()
                .subjectName(subject.getName())
                .subjectId(subject.getId())
                .dates(dates)
                .students(studentRows)
                .averageGrades(averageGrades)
                .build();
    }

    private float calculateAverageGrade(List<Grade> grades) {
        if (grades.isEmpty()) {
            return 0.0f;
        }
        
        float weightedSum = 0;
        float totalWeight = 0;
        
        for (Grade grade : grades) {
            if (!grade.isFinal()) {
                weightedSum += grade.getGradeValue() * grade.getWeight();
                totalWeight += grade.getWeight();
            }
        }
        
        return totalWeight > 0 ? weightedSum / totalWeight : 0.0f;
    }

    private Integer getFinalGrade(List<Grade> grades) {
        return grades.stream()
                .filter(Grade::isFinal)
                .map(Grade::getGradeValue)
                .findFirst()
                .orElse(null);
    }
} 