package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.HomeworkDto;
import com.fivednevnik.api.dto.HomeworkStatusDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<HomeworkDto> getHomeworkForStudent(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        if (student.getClassName() == null || student.getClassName().isEmpty()) {
            return new ArrayList<>();
        }
        return getMockHomework().stream()
                .filter(hw -> student.getClassName().equals(hw.getClassName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HomeworkDto> getHomeworkForStudentByPeriod(String username, LocalDate from, LocalDate to) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        if (student.getClassName() == null || student.getClassName().isEmpty()) {
            return new ArrayList<>();
        }
        return getMockHomework().stream()
                .filter(hw -> student.getClassName().equals(hw.getClassName()))
                .filter(hw -> {
                    LocalDate dueDate = hw.getDueDate().toLocalDate();
                    return !dueDate.isBefore(from) && !dueDate.isAfter(to);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HomeworkDto> getHomeworkByClassId(Long classId) {

        return getMockHomework().stream()
                .filter(hw -> classId.equals(hw.getClassId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HomeworkDto> getHomeworkByClassName(String className) {

        return getMockHomework().stream()
                .filter(hw -> className.equals(hw.getClassName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HomeworkDto> getHomeworkBySubjectId(Long subjectId) {
        return getMockHomework().stream()
                .filter(hw -> subjectId.equals(hw.getSubjectId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public HomeworkDto createHomework(HomeworkDto homeworkDto, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));
        

        if (homeworkDto.getTeacherId() == null) {
            homeworkDto.setTeacherId(teacher.getId());
        }
        
        homeworkDto.setTeacherName(teacher.getLastName() + " " + teacher.getFirstName());
        homeworkDto.setCreatedAt(LocalDateTime.now());

        homeworkDto.setId(1L);
        return homeworkDto;
    }

    @Transactional
    public HomeworkDto updateHomework(Long id, HomeworkDto homeworkDto, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));

        homeworkDto.setId(id);
        return homeworkDto;
    }
    

    @Transactional
    public void deleteHomework(Long id, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден: " + teacherUsername));

    }

    @Transactional
    public HomeworkStatusDto updateHomeworkStatus(Long homeworkId, HomeworkStatusDto statusDto, String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден: " + studentUsername));
        statusDto.setId(1L);
        statusDto.setHomeworkId(homeworkId);
        statusDto.setStudentId(student.getId());
        statusDto.setStudentName(student.getLastName() + " " + student.getFirstName());
        statusDto.setCompletedAt(LocalDateTime.now());
        return statusDto;
    }

    @Transactional(readOnly = true)
    public List<HomeworkStatusDto> getHomeworkStatuses(Long homeworkId) {
        return getMockHomeworkStatuses(homeworkId);
    }

    private List<HomeworkDto> getMockHomework() {
        List<HomeworkDto> homework = new ArrayList<>();
        

        homework.add(HomeworkDto.builder()
                .id(1L)
                .classId(1L)
                .className("9А")
                .subjectId(1L)
                .subjectName("Математика")
                .teacherId(2L)
                .teacherName("Елена Петрова")
                .title("Решение квадратных уравнений")
                .description("Решить задачи №125-130 из учебника")
                .dueDate(LocalDateTime.of(2024, 6, 1, 0, 0))
                .createdAt(LocalDateTime.of(2024, 5, 25, 14, 30))
                .build());

        homework.add(HomeworkDto.builder()
                .id(2L)
                .classId(1L)
                .className("9А")
                .subjectId(2L)
                .subjectName("Русский язык")
                .teacherId(3L)
                .teacherName("Сергей Иванов")
                .title("Подготовка к сочинению")
                .description("Подготовить план сочинения на тему \"Роль книги в жизни человека\"")
                .dueDate(LocalDateTime.of(2024, 6, 2, 0, 0))
                .createdAt(LocalDateTime.of(2024, 5, 26, 11, 15))
                .build());

        homework.add(HomeworkDto.builder()
                .id(3L)
                .classId(1L)
                .className("9А")
                .subjectId(3L)
                .subjectName("Физика")
                .teacherId(4L)
                .teacherName("Анна Смирнова")
                .title("Законы сохранения в механике")
                .description("Решить задачи из сборника №45-50")
                .dueDate(LocalDateTime.of(2024, 6, 3, 0, 0))
                .createdAt(LocalDateTime.of(2024, 5, 27, 12, 0))
                .build());

        homework.add(HomeworkDto.builder()
                .id(4L)
                .classId(2L)
                .className("10Б")
                .subjectId(1L)
                .subjectName("Математика")
                .teacherId(2L)
                .teacherName("Елена Петрова")
                .title("Интегралы")
                .description("Выполнить задания из учебника стр. 234-235")
                .dueDate(LocalDateTime.of(2024, 6, 1, 0, 0))
                .createdAt(LocalDateTime.of(2024, 5, 25, 15, 45))
                .build());
        
        return homework;
    }

    private List<HomeworkStatusDto> getMockHomeworkStatuses(Long homeworkId) {
        List<HomeworkStatusDto> statuses = new ArrayList<>();
        
        statuses.add(HomeworkStatusDto.builder()
                .id(1L)
                .homeworkId(homeworkId)
                .studentId(5L)
                .studentName("Иван Смирнов")
                .isCompleted(true)
                .completedAt(LocalDateTime.of(2024, 5, 28, 18, 30))
                .comment("Выполнено полностью")
                .build());
        
        statuses.add(HomeworkStatusDto.builder()
                .id(2L)
                .homeworkId(homeworkId)
                .studentId(6L)
                .studentName("Мария Иванова")
                .isCompleted(true)
                .completedAt(LocalDateTime.of(2024, 5, 29, 19, 15))
                .build());
        
        statuses.add(HomeworkStatusDto.builder()
                .id(3L)
                .homeworkId(homeworkId)
                .studentId(7L)
                .studentName("Алексей Петров")
                .isCompleted(false)
                .build());
        
        return statuses;
    }
} 
