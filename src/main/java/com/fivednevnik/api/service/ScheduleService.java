package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.ScheduleItemDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с расписанием занятий
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final UserRepository userRepository;

    /**
     * Получение расписания для пользователя
     */
    @Transactional(readOnly = true)
    public List<ScheduleItemDto> getScheduleForUser(String username) {
        log.debug("Получение расписания для пользователя: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));

        if (user.getRole() == User.Role.TEACHER || user.getRole() == User.Role.CLASS_TEACHER) {
            return getScheduleByTeacherId(user.getId());
        }

        if (user.getRole() == User.Role.STUDENT) {
            return getScheduleByClassName(user.getClassName());
        }

        return new ArrayList<>();
    }
    
    /**
     * Получение расписания по имени класса
     */
    @Transactional(readOnly = true)
    public List<ScheduleItemDto> getScheduleByClassName(String className) {
        log.debug("Получение расписания для класса: {}", className);
        // TODO: Реализовать получение расписания из репозитория
        return getMockSchedule().stream()
                .filter(item -> className.equals(item.getClassName()))
                .collect(Collectors.toList());
    }
    
    /**
     * Получение расписания по ID учителя
     */
    @Transactional(readOnly = true)
    public List<ScheduleItemDto> getScheduleByTeacherId(Long teacherId) {
        log.debug("Получение расписания для учителя с ID: {}", teacherId);
        if (!userRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Учитель не найден с ID: " + teacherId);
        }
        
        // TODO: Реализовать получение расписания из репозитория
        return getMockSchedule().stream()
                .filter(item -> teacherId.equals(item.getTeacherId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Получение расписания по дню недели и классу
     */
    @Transactional(readOnly = true)
    public List<ScheduleItemDto> getScheduleByDayAndClass(Integer dayOfWeek, String className) {
        log.debug("Получение расписания на день {} для класса {}", dayOfWeek, className);
        
        List<ScheduleItemDto> result = getMockSchedule().stream()
                .filter(item -> dayOfWeek.equals(item.getDayOfWeek()))
                .collect(Collectors.toList());
        
        if (className != null && !className.isEmpty()) {
            result = result.stream()
                    .filter(item -> className.equals(item.getClassName()))
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    /**
     * Создание нового элемента расписания
     */
    @Transactional
    public ScheduleItemDto createScheduleItem(ScheduleItemDto scheduleItemDto) {
        log.debug("Создание нового элемента расписания: {}", scheduleItemDto);
        
        // TODO: Реализовать сохранение элемента расписания в репозиторий
        scheduleItemDto.setId(1L); // Временный ID для мока
        return scheduleItemDto;
    }
    
    /**
     * Обновление элемента расписания
     */
    @Transactional
    public ScheduleItemDto updateScheduleItem(Long id, ScheduleItemDto scheduleItemDto) {
        log.debug("Обновление элемента расписания с ID: {}", id);
        
        // TODO: Реализовать обновление элемента расписания в репозитории
        scheduleItemDto.setId(id);
        return scheduleItemDto;
    }
    
    /**
     * Удаление элемента расписания
     */
    @Transactional
    public void deleteScheduleItem(Long id) {
        log.debug("Удаление элемента расписания с ID: {}", id);
        
        // TODO: Реализовать удаление элемента расписания из репозитория
    }
    
    /**
     * Временный метод для создания тестовых данных
     */
    private List<ScheduleItemDto> getMockSchedule() {
        List<ScheduleItemDto> schedule = new ArrayList<>();
        
        // Класс 9А
        schedule.add(ScheduleItemDto.builder()
                .id(1L)
                .classId(1L)
                .className("9А")
                .subjectId(1L)
                .subjectName("Математика")
                .teacherId(2L)
                .teacherName("Елена Петрова")
                .dayOfWeek(1) // Понедельник
                .lessonNumber(1)
                .startTime("08:30")
                .endTime("09:15")
                .classroom("301")
                .build());
        
        schedule.add(ScheduleItemDto.builder()
                .id(2L)
                .classId(1L)
                .className("9А")
                .subjectId(2L)
                .subjectName("Русский язык")
                .teacherId(3L)
                .teacherName("Сергей Иванов")
                .dayOfWeek(1) // Понедельник
                .lessonNumber(2)
                .startTime("09:25")
                .endTime("10:10")
                .classroom("201")
                .build());
        
        schedule.add(ScheduleItemDto.builder()
                .id(3L)
                .classId(1L)
                .className("9А")
                .subjectId(3L)
                .subjectName("Физика")
                .teacherId(4L)
                .teacherName("Анна Смирнова")
                .dayOfWeek(1) // Понедельник
                .lessonNumber(3)
                .startTime("10:20")
                .endTime("11:05")
                .classroom("401")
                .build());
        
        // Класс 10Б
        schedule.add(ScheduleItemDto.builder()
                .id(4L)
                .classId(2L)
                .className("10Б")
                .subjectId(1L)
                .subjectName("Математика")
                .teacherId(2L)
                .teacherName("Елена Петрова")
                .dayOfWeek(2) // Вторник
                .lessonNumber(1)
                .startTime("08:30")
                .endTime("09:15")
                .classroom("302")
                .build());
        
        schedule.add(ScheduleItemDto.builder()
                .id(5L)
                .classId(2L)
                .className("10Б")
                .subjectId(4L)
                .subjectName("Химия")
                .teacherId(5L)
                .teacherName("Игорь Кузнецов")
                .dayOfWeek(2) // Вторник
                .lessonNumber(2)
                .startTime("09:25")
                .endTime("10:10")
                .classroom("405")
                .build());
        
        return schedule;
    }
} 