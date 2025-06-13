package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.AcademicReportDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.AcademicPeriod;
import com.fivednevnik.api.model.Grade;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.AcademicPeriodRepository;
import com.fivednevnik.api.repository.AttendanceRepository;
import com.fivednevnik.api.repository.GradeRepository;
import com.fivednevnik.api.repository.SubjectRepository;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicReportService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    public AcademicReportDto generateClassReport(String className, Long periodId) {

        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Период не найден с ID: " + periodId));

        List<User> students = userRepository.findByRoleAndClassName(User.Role.STUDENT, className);
        if (students.isEmpty()) {
            throw new ResourceNotFoundException("Ученики не найдены для класса: " + className);
        }

        List<Subject> subjects = subjectRepository.findAll();

        AcademicReportDto report = AcademicReportDto.builder()
                .reportName("Отчет об успеваемости")
                .periodName(period.getName())
                .periodId(period.getId())
                .className(className)
                .build();

        List<AcademicReportDto.StudentStatistics> studentStatsList = generateStudentStatistics(students, subjects, period);
        report.setStudentStatistics(studentStatsList);

        List<AcademicReportDto.SubjectStatistics> subjectStatsList = generateSubjectStatistics(className, subjects, period);
        report.setSubjectStatistics(subjectStatsList);

        AcademicReportDto.ClassStatistics classStats = generateClassStatistics(studentStatsList);
        report.setClassStatistics(classStats);
        
        return report;
    }

    private List<AcademicReportDto.StudentStatistics> generateStudentStatistics(
            List<User> students, List<Subject> subjects, AcademicPeriod period) {
        
        List<AcademicReportDto.StudentStatistics> result = new ArrayList<>();
        
        for (User student : students) {
            Map<String, Float> subjectAverages = new HashMap<>();
            Map<String, Integer> finalGrades = new HashMap<>();
            float totalAverage = 0;
            int subjectsWithGrades = 0;
            
            for (Subject subject : subjects) {
                List<Grade> grades = gradeRepository.findByStudentAndSubjectAndPeriod(
                        student, subject, period.getName());
                
                if (!grades.isEmpty()) {
                    float sum = 0;
                    float weightSum = 0;
                    
                    for (Grade grade : grades) {
                        if (!grade.isFinal()) {
                            float weight = grade.getWeight();
                            sum += grade.getGradeValue() * weight;
                            weightSum += weight;
                        }
                    }
                    
                    float average = weightSum > 0 ? sum / weightSum : 0;
                    subjectAverages.put(subject.getName(), average);
                    totalAverage += average;
                    subjectsWithGrades++;

                    grades.stream()
                            .filter(Grade::isFinal)
                            .findFirst()
                            .ifPresent(grade -> finalGrades.put(subject.getName(), grade.getGradeValue()));
                }
            }

            float studentAverage = subjectsWithGrades > 0 ? totalAverage / subjectsWithGrades : 0;

            int absencesCount = attendanceRepository.countByStudentAndPeriodAndIsPresent(
                    student, period.getName(), false);
            
            AcademicReportDto.StudentStatistics stats = AcademicReportDto.StudentStatistics.builder()
                    .studentId(student.getId())
                    .studentName(student.getFullName())
                    .averageGrade(studentAverage)
                    .subjectAverages(subjectAverages)
                    .finalGrades(finalGrades)
                    .absencesCount(absencesCount)
                    .build();
            
            result.add(stats);
        }

        result.sort(Comparator.comparing(AcademicReportDto.StudentStatistics::getAverageGrade).reversed());
        
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setClassRank(i + 1);
        }
        
        return result;
    }

    private List<AcademicReportDto.SubjectStatistics> generateSubjectStatistics(
            String className, List<Subject> subjects, AcademicPeriod period) {
        
        List<AcademicReportDto.SubjectStatistics> result = new ArrayList<>();
        
        for (Subject subject : subjects) {
            List<Grade> grades = gradeRepository.findByClassNameAndSubjectAndPeriod(
                    className, subject, period.getName());
            
            if (!grades.isEmpty()) {
                int excellent = 0;
                int good = 0;
                int average = 0;
                int poor = 0;
                
                for (Grade grade : grades) {
                    if (!grade.isFinal()) {
                        switch (grade.getGradeValue()) {
                            case 5:
                                excellent++;
                                break;
                            case 4:
                                good++;
                                break;
                            case 3:
                                average++;
                                break;
                            case 2:
                            case 1:
                                poor++;
                                break;
                        }
                    }
                }
                
                int totalGrades = excellent + good + average + poor;

                float sum = excellent * 5 + good * 4 + average * 3 + poor * 2;
                float subjectAverage = totalGrades > 0 ? sum / totalGrades : 0;

                float performancePercentage = totalGrades > 0 ? 
                        (float) (excellent + good + average) / totalGrades * 100 : 0;
                float qualityPercentage = totalGrades > 0 ? 
                        (float) (excellent + good) / totalGrades * 100 : 0;
                
                AcademicReportDto.SubjectStatistics stats = AcademicReportDto.SubjectStatistics.builder()
                        .subjectId(subject.getId())
                        .subjectName(subject.getName())
                        .averageGrade(subjectAverage)
                        .excellentCount(excellent)
                        .goodCount(good)
                        .averageCount(average)
                        .poorCount(poor)
                        .performancePercentage(performancePercentage)
                        .qualityPercentage(qualityPercentage)
                        .build();
                
                result.add(stats);
            }
        }
        
        return result;
    }

    private AcademicReportDto.ClassStatistics generateClassStatistics(
            List<AcademicReportDto.StudentStatistics> studentStats) {
        
        if (studentStats.isEmpty()) {
            return AcademicReportDto.ClassStatistics.builder()
                    .averageGrade(0f)
                    .excellentStudentsCount(0)
                    .goodStudentsCount(0)
                    .averageStudentsCount(0)
                    .underperformingStudentsCount(0)
                    .performancePercentage(0f)
                    .qualityPercentage(0f)
                    .build();
        }
        
        int excellentCount = 0;
        int goodCount = 0;
        int averageCount = 0;
        int underperformingCount = 0;
        
        float totalAverage = 0;
        
        for (AcademicReportDto.StudentStatistics student : studentStats) {
            float avg = student.getAverageGrade();
            totalAverage += avg;
            
            if (avg >= 4.5f) {
                excellentCount++;
            } else if (avg >= 3.5f) {
                goodCount++;
            } else if (avg >= 2.5f) {
                averageCount++;
            } else {
                underperformingCount++;
            }
        }
        
        int totalStudents = studentStats.size();
        float classAverage = totalStudents > 0 ? totalAverage / totalStudents : 0;
        
        float performancePercentage = totalStudents > 0 ? 
                (float) (excellentCount + goodCount + averageCount) / totalStudents * 100 : 0;
        float qualityPercentage = totalStudents > 0 ? 
                (float) (excellentCount + goodCount) / totalStudents * 100 : 0;
        
        return AcademicReportDto.ClassStatistics.builder()
                .averageGrade(classAverage)
                .excellentStudentsCount(excellentCount)
                .goodStudentsCount(goodCount)
                .averageStudentsCount(averageCount)
                .underperformingStudentsCount(underperformingCount)
                .performancePercentage(performancePercentage)
                .qualityPercentage(qualityPercentage)
                .build();
    }

    @Transactional(readOnly = true)
    public AcademicReportDto.StudentStatistics generateStudentReport(Long studentId, Long periodId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + studentId));

        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Период не найден с ID: " + periodId));

        List<Subject> subjects = subjectRepository.findAll();
        
        Map<String, Float> subjectAverages = new HashMap<>();
        Map<String, Integer> finalGrades = new HashMap<>();
        float totalAverage = 0;
        int subjectsWithGrades = 0;
        
        for (Subject subject : subjects) {
            List<Grade> grades = gradeRepository.findByStudentAndSubjectAndPeriod(
                    student, subject, period.getName());
            
            if (!grades.isEmpty()) {
                float sum = 0;
                float weightSum = 0;
                
                for (Grade grade : grades) {
                    if (!grade.isFinal()) {
                        float weight = grade.getWeight();
                        sum += grade.getGradeValue() * weight;
                        weightSum += weight;
                    }
                }
                
                float average = weightSum > 0 ? sum / weightSum : 0;
                subjectAverages.put(subject.getName(), average);
                totalAverage += average;
                subjectsWithGrades++;
                grades.stream()
                        .filter(Grade::isFinal)
                        .findFirst()
                        .ifPresent(grade -> finalGrades.put(subject.getName(), grade.getGradeValue()));
            }
        }

        float studentAverage = subjectsWithGrades > 0 ? totalAverage / subjectsWithGrades : 0;

        int absencesCount = attendanceRepository.countByStudentAndPeriodAndIsPresent(
                student, period.getName(), false);

        List<User> classmates = userRepository.findByRoleAndClassName(User.Role.STUDENT, student.getClassName());

        Map<Long, Float> classmateAverages = new HashMap<>();
        for (User classmate : classmates) {
            float classmateTotal = 0;
            int classmateSubjects = 0;
            
            for (Subject subject : subjects) {
                List<Grade> grades = gradeRepository.findByStudentAndSubjectAndPeriod(
                        classmate, subject, period.getName());
                
                if (!grades.isEmpty()) {
                    float sum = 0;
                    float weightSum = 0;
                    
                    for (Grade grade : grades) {
                        if (!grade.isFinal()) {
                            float weight = grade.getWeight();
                            sum += grade.getGradeValue() * weight;
                            weightSum += weight;
                        }
                    }
                    
                    float average = weightSum > 0 ? sum / weightSum : 0;
                    classmateTotal += average;
                    classmateSubjects++;
                }
            }
            
            float classmateAverage = classmateSubjects > 0 ? classmateTotal / classmateSubjects : 0;
            classmateAverages.put(classmate.getId(), classmateAverage);
        }

        List<Map.Entry<Long, Float>> sortedClassmates = classmateAverages.entrySet().stream()
                .sorted(Map.Entry.<Long, Float>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        int rank = 1;
        for (Map.Entry<Long, Float> entry : sortedClassmates) {
            if (entry.getKey().equals(studentId)) {
                break;
            }
            rank++;
        }
        
        return AcademicReportDto.StudentStatistics.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .averageGrade(studentAverage)
                .subjectAverages(subjectAverages)
                .finalGrades(finalGrades)
                .absencesCount(absencesCount)
                .classRank(rank)
                .build();
    }
} 