package com.fivednevnik.api.repository;

import com.fivednevnik.api.model.Schedule;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByClassName(String className);

    List<Schedule> findByClassNameAndDayOfWeek(String className, Integer dayOfWeek);

    List<Schedule> findByClassNameAndDayOfWeekAndIsActive(String className, Integer dayOfWeek, boolean isActive);

    List<Schedule> findByTeacher(User teacher);

    List<Schedule> findByTeacherAndDayOfWeek(User teacher, Integer dayOfWeek);

    List<Schedule> findBySubject(Subject subject);

    List<Schedule> findByClassNameAndSubject(String className, Subject subject);

    List<Schedule> findByClassNameAndSubjectAndDayOfWeek(String className, Subject subject, Integer dayOfWeek);

    List<Schedule> findByIsActiveTrue();
} 