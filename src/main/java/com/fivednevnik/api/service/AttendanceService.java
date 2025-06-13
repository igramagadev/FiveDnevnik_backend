package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.AttendanceDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface AttendanceService {


    List<AttendanceDto> getAllAttendance(Date startDate, Date endDate, Long subjectId);
    

    AttendanceDto getAttendanceById(Long id);

    List<AttendanceDto> getAttendanceByStudentId(Long studentId);

    List<AttendanceDto> getAttendanceByClassId(Long classId);

    List<AttendanceDto> getAttendanceByClassName(String className);

    List<AttendanceDto> getAttendanceByStudentIdAndDate(Long studentId, LocalDate date);

    List<AttendanceDto> getAttendanceByClassNameAndDate(String className, LocalDate date);

    List<AttendanceDto> getAttendanceByStudentIdAndDateRange(Long studentId, LocalDate fromDate, LocalDate toDate);

    List<AttendanceDto> getAttendanceByClassNameAndDateRange(String className, LocalDate fromDate, LocalDate toDate);

    AttendanceDto createAttendance(AttendanceDto attendanceDto);

    AttendanceDto updateAttendance(Long id, AttendanceDto attendanceDto);

    void deleteAttendance(Long id);

    Map<String, Object> getAttendanceStats(Long userId, Long periodId, Long subjectId);
} 
