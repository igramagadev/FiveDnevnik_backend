package com.fivednevnik.backend.repository;

import com.fivednevnik.backend.model.SchoolClass;
import com.fivednevnik.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findByClassTeacher(User teacher);
    List<SchoolClass> findByStudentsContaining(User student);
    List<SchoolClass> findByGrade(Integer grade);
} 