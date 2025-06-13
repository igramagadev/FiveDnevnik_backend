package com.fivednevnik.api.service.impl;

import com.fivednevnik.api.dto.AttendanceDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.AcademicPeriod;
import com.fivednevnik.api.model.Attendance;
import com.fivednevnik.api.model.Class;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.AcademicPeriodRepository;
import com.fivednevnik.api.repository.AttendanceRepository;
import com.fivednevnik.api.repository.ClassRepository;
import com.fivednevnik.api.repository.SubjectRepository;
import com.fivednevnik.api.repository.UserRepository;
import com.fivednevnik.api.service.AttendanceService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final ClassRepository classRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAllAttendance(Date startDate, Date endDate, Long subjectId) {
        return attendanceRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDto getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Запись посещаемости не найдена с ID: " + id));
        return mapToDto(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByStudentId(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + studentId));
        return attendanceRepository.findByStudent(student).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByClassId(Long classId) {
        return attendanceRepository.findByClassId(classId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByClassName(String className) {
        return attendanceRepository.findByClassName(className).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByStudentIdAndDate(Long studentId, LocalDate date) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + studentId));
        return attendanceRepository.findByStudentAndDate(student, date).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByClassNameAndDate(String className, LocalDate date) {
        return attendanceRepository.findByClassNameAndDate(className, date).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByStudentIdAndDateRange(Long studentId, LocalDate fromDate, LocalDate toDate) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + studentId));
        return attendanceRepository.findByStudentAndDateBetween(student, fromDate, toDate).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDto> getAttendanceByClassNameAndDateRange(String className, LocalDate fromDate, LocalDate toDate) {
        return attendanceRepository.findByClassNameAndDateBetween(className, fromDate, toDate).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttendanceDto createAttendance(AttendanceDto attendanceDto) {
        
        User student = userRepository.findById(attendanceDto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + attendanceDto.getStudentId()));
        
        Subject subject = subjectRepository.findById(attendanceDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + attendanceDto.getSubjectId()));
        
        User teacher = userRepository.findById(attendanceDto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + attendanceDto.getTeacherId()));
        
        AcademicPeriod academicPeriod = null;
        if (attendanceDto.getAcademicPeriodId() != null) {
            academicPeriod = academicPeriodRepository.findById(attendanceDto.getAcademicPeriodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Академический период не найден с ID: " + attendanceDto.getAcademicPeriodId()));
        } else {
            LocalDate date = attendanceDto.getDate();
            if (date != null) {
                academicPeriod = academicPeriodRepository.findByDate(date).stream()
                        .findFirst()
                        .orElse(null);
            }
        }
        
        Attendance attendance = Attendance.builder()
                .student(student)
                .subject(subject)
                .teacher(teacher)
                .date(attendanceDto.getDate())
                .isPresent(attendanceDto.isPresent())
                .reason(attendanceDto.getReason())
                .isExcused(attendanceDto.isExcused())
                .lessonNumber(attendanceDto.getLessonNumber())
                .period(attendanceDto.getPeriod())
                .academicPeriod(academicPeriod)
                .build();
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToDto(savedAttendance);
    }

    @Override
    @Transactional
    public AttendanceDto updateAttendance(Long id, AttendanceDto attendanceDto) {
        
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Запись посещаемости не найдена с ID: " + id));
        
        if (attendanceDto.getStudentId() != null) {
            User student = userRepository.findById(attendanceDto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден с ID: " + attendanceDto.getStudentId()));
            attendance.setStudent(student);
        }
        
        if (attendanceDto.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(attendanceDto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден с ID: " + attendanceDto.getSubjectId()));
            attendance.setSubject(subject);
        }
        
        if (attendanceDto.getTeacherId() != null) {
            User teacher = userRepository.findById(attendanceDto.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + attendanceDto.getTeacherId()));
            attendance.setTeacher(teacher);
        }
        
        if (attendanceDto.getDate() != null) {
            attendance.setDate(attendanceDto.getDate());
        }
        
        attendance.setPresent(attendanceDto.isPresent());
        
        if (attendanceDto.getReason() != null) {
            attendance.setReason(attendanceDto.getReason());
        }
        
        attendance.setExcused(attendanceDto.isExcused());
        
        if (attendanceDto.getLessonNumber() != null) {
            attendance.setLessonNumber(attendanceDto.getLessonNumber());
        }
        
        if (attendanceDto.getPeriod() != null) {
            attendance.setPeriod(attendanceDto.getPeriod());
        }
        
        if (attendanceDto.getAcademicPeriodId() != null) {
            AcademicPeriod academicPeriod = academicPeriodRepository.findById(attendanceDto.getAcademicPeriodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Академический период не найден с ID: " + attendanceDto.getAcademicPeriodId()));
            attendance.setAcademicPeriod(academicPeriod);
        }
        
        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return mapToDto(updatedAttendance);
    }

    @Override
    @Transactional
    public void deleteAttendance(Long id) {
        
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Запись посещаемости не найдена с ID: " + id);
        }
        
        attendanceRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> getAttendanceStats(Long userId, Long periodId, Long subjectId) {

        return Map.of(
            "totalLessons", 0,
            "presentCount", 0,
            "absentCount", 0,
            "excusedCount", 0,
            "attendanceRate", 0.0,
            "excusedRate", 0.0
        );
    }

    private AttendanceDto mapToDto(Attendance attendance) {
        AttendanceDto.AttendanceDtoBuilder builder = AttendanceDto.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getFullName())
                .subjectId(attendance.getSubject().getId())
                .subjectName(attendance.getSubject().getName())
                .date(attendance.getDate())
                .isPresent(attendance.isPresent())
                .reason(attendance.getReason())
                .isExcused(attendance.isExcused())
                .teacherId(attendance.getTeacher().getId())
                .teacherName(attendance.getTeacher().getFullName())
                .lessonNumber(attendance.getLessonNumber())
                .period(attendance.getPeriod())
                .className(attendance.getStudent().getClassName());
        
        if (attendance.getAcademicPeriod() != null) {
            builder.academicPeriodId(attendance.getAcademicPeriod().getId())
                   .academicPeriodName(attendance.getAcademicPeriod().getName());
        }
        
        return builder.build();
    }
} 
