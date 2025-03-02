package com.fivednevnik.backend.repository;

import com.fivednevnik.backend.model.Schedule;
import com.fivednevnik.backend.model.SchoolClass;
import com.fivednevnik.backend.model.Subject;
import com.fivednevnik.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByTeacher(User teacher);
    List<Schedule> findBySchoolClass(SchoolClass schoolClass);
    List<Schedule> findBySubject(Subject subject);
    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);
    List<Schedule> findBySchoolClassAndDayOfWeek(SchoolClass schoolClass, DayOfWeek dayOfWeek);
    List<Schedule> findByTeacherAndDayOfWeek(User teacher, DayOfWeek dayOfWeek);
} 