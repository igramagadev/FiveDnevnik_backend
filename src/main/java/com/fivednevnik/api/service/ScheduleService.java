package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.ScheduleDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.Schedule;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.ScheduleRepository;
import com.fivednevnik.api.repository.SubjectRepository;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleDto getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Расписание не найдено с ID: " + id));
        return mapToDto(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByClassName(String className) {
        return scheduleRepository.findByClassName(className).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByClassNameAndDayOfWeek(String className, Integer dayOfWeek) {
        return scheduleRepository.findByClassNameAndDayOfWeek(className, dayOfWeek).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByTeacherId(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + teacherId));
        return scheduleRepository.findByTeacher(teacher).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByTeacherIdAndDayOfWeek(Long teacherId, Integer dayOfWeek) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + teacherId));
        return scheduleRepository.findByTeacherAndDayOfWeek(teacher, dayOfWeek).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByStudentId(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + studentId));

        String className = student.getClassName();
        if (className == null || className.isEmpty()) {
            throw new ResourceNotFoundException("Ученик с ID: " + studentId + " не привязан к классу");
        }
        
        return scheduleRepository.findByClassName(className).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByStudentIdAndDayOfWeek(Long studentId, Integer dayOfWeek) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + studentId));
        String className = student.getClassName();
        if (className == null || className.isEmpty()) {
            throw new ResourceNotFoundException("Ученик с ID: " + studentId + " не привязан к классу");
        }
        
        return scheduleRepository.findByClassNameAndDayOfWeek(className, dayOfWeek).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesBySubjectId(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
        return scheduleRepository.findBySubject(subject).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleDto> getSchedulesByClassNameAndSubjectId(String className, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + subjectId));
        return scheduleRepository.findByClassNameAndSubject(className, subject).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleDto createSchedule(ScheduleDto scheduleDto) {
        
        Subject subject = subjectRepository.findById(scheduleDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + scheduleDto.getSubjectId()));
        
        User teacher = userRepository.findById(scheduleDto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + scheduleDto.getTeacherId()));
        
        Schedule schedule = Schedule.builder()
                .className(scheduleDto.getClassName())
                .subject(subject)
                .teacher(teacher)
                .dayOfWeek(scheduleDto.getDayOfWeek())
                .lessonNumber(scheduleDto.getLessonNumber())
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .classroom(scheduleDto.getClassroom())
                .isActive(scheduleDto.isActive())
                .build();
        
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return mapToDto(savedSchedule);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long id, ScheduleDto scheduleDto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Расписание не найдено с ID: " + id));
        
        Subject subject = subjectRepository.findById(scheduleDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + scheduleDto.getSubjectId()));
        
        User teacher = userRepository.findById(scheduleDto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + scheduleDto.getTeacherId()));
        
        schedule.setClassName(scheduleDto.getClassName());
        schedule.setSubject(subject);
        schedule.setTeacher(teacher);
        schedule.setDayOfWeek(scheduleDto.getDayOfWeek());
        schedule.setLessonNumber(scheduleDto.getLessonNumber());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setClassroom(scheduleDto.getClassroom());
        schedule.setActive(scheduleDto.isActive());
        
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return mapToDto(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {

        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Расписание не найдено с ID: " + id);
        }
        
        scheduleRepository.deleteById(id);
    }

    @Transactional
    public ScheduleDto toggleScheduleActive(Long id, boolean isActive) {
        
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Расписание не найдено с ID: " + id));
        
        schedule.setActive(isActive);
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        
        return mapToDto(updatedSchedule);
    }

    private ScheduleDto mapToDto(Schedule schedule) {
        ScheduleDto dto = ScheduleDto.builder()
                .id(schedule.getId())
                .className(schedule.getClassName())
                .subjectId(schedule.getSubject().getId())
                .subjectName(schedule.getSubject().getName())
                .teacherId(schedule.getTeacher().getId())
                .teacherName(schedule.getTeacher().getFullName())
                .dayOfWeek(schedule.getDayOfWeek())
                .dayOfWeekName(DayOfWeek.of(schedule.getDayOfWeek()).getDisplayName(TextStyle.FULL, new Locale("ru")))
                .lessonNumber(schedule.getLessonNumber())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .classroom(schedule.getClassroom())
                .isActive(schedule.isActive())
                .build();
        
        return dto;
    }
} 
