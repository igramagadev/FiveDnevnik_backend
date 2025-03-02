package com.fivednevnik.backend.service;

import com.fivednevnik.backend.dto.LessonDto;
import com.fivednevnik.backend.model.Lesson;
import com.fivednevnik.backend.model.SchoolClass;
import com.fivednevnik.backend.model.Subject;
import com.fivednevnik.backend.model.User;
import com.fivednevnik.backend.repository.LessonRepository;
import com.fivednevnik.backend.repository.SchoolClassRepository;
import com.fivednevnik.backend.repository.SubjectRepository;
import com.fivednevnik.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    public List<LessonDto> getLessonsByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Учитель не найден с ID: " + teacherId));
        
        return lessonRepository.findByTeacher(teacher).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LessonDto> getLessonsByClass(Long classId) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден с ID: " + classId));
        
        return lessonRepository.findBySchoolClass(schoolClass).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LessonDto> getLessonsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return lessonRepository.findByStartTimeBetween(startDateTime, endDateTime).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LessonDto> getLessonsByClassAndDateRange(Long classId, LocalDate startDate, LocalDate endDate) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Класс не найден с ID: " + classId));
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return lessonRepository.findBySchoolClassAndStartTimeBetween(schoolClass, startDateTime, endDateTime).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LessonDto createLesson(LessonDto lessonDto) {
        Subject subject = subjectRepository.findById(lessonDto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден с ID: " + lessonDto.getSubjectId()));
        
        SchoolClass schoolClass = schoolClassRepository.findById(lessonDto.getClassId())
                .orElseThrow(() -> new RuntimeException("Класс не найден с ID: " + lessonDto.getClassId()));
        
        User teacher = userRepository.findById(lessonDto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Учитель не найден с ID: " + lessonDto.getTeacherId()));
        
        Lesson lesson = new Lesson();
        lesson.setSubject(subject);
        lesson.setSchoolClass(schoolClass);
        lesson.setTeacher(teacher);
        lesson.setStartTime(lessonDto.getStartTime());
        lesson.setEndTime(lessonDto.getEndTime());
        lesson.setTopic(lessonDto.getTopic());
        lesson.setHomework(lessonDto.getHomework());
        lesson.setRoomNumber(lessonDto.getRoomNumber());
        
        Lesson savedLesson = lessonRepository.save(lesson);
        return convertToDto(savedLesson);
    }

    public LessonDto updateLesson(Long id, LessonDto lessonDto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Урок не найден с ID: " + id));
        
        lesson.setTopic(lessonDto.getTopic());
        lesson.setHomework(lessonDto.getHomework());
        lesson.setRoomNumber(lessonDto.getRoomNumber());
        
        if (lessonDto.getStartTime() != null) {
            lesson.setStartTime(lessonDto.getStartTime());
        }
        
        if (lessonDto.getEndTime() != null) {
            lesson.setEndTime(lessonDto.getEndTime());
        }
        
        Lesson updatedLesson = lessonRepository.save(lesson);
        return convertToDto(updatedLesson);
    }

    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new RuntimeException("Урок не найден с ID: " + id);
        }
        lessonRepository.deleteById(id);
    }

    private LessonDto convertToDto(Lesson lesson) {
        return new LessonDto(
                lesson.getId(),
                lesson.getSubject().getId(),
                lesson.getSubject().getName(),
                lesson.getSchoolClass().getId(),
                lesson.getSchoolClass().getName(),
                lesson.getTeacher().getId(),
                lesson.getTeacher().getFullName(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getTopic(),
                lesson.getHomework(),
                lesson.getRoomNumber()
        );
    }
} 