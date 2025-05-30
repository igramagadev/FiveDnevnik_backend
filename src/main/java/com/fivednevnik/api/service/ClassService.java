package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.ClassDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.Class;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.ClassRepository;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с классами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClassService {

    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    /**
     * Получение всех классов
     */
    @Transactional(readOnly = true)
    public List<ClassDto> getAllClasses() {
        log.debug("Получение списка всех классов");
        return classRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение класса по ID
     */
    @Transactional(readOnly = true)
    public ClassDto getClassById(Long id) {
        log.debug("Поиск класса по ID: {}", id);
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Класс не найден с ID: " + id));
        return mapToDto(classEntity);
    }

    /**
     * Создание нового класса
     */
    @Transactional
    public ClassDto createClass(ClassDto classDto) {
        log.debug("Создание нового класса: {}", classDto.getName());

        if (classRepository.existsByName(classDto.getName())) {
            throw new IllegalArgumentException("Класс с таким именем уже существует");
        }

        User classTeacher = null;
        if (classDto.getClassTeacherId() != null) {
            classTeacher = userRepository.findById(classDto.getClassTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + classDto.getClassTeacherId()));

            if (classTeacher.getRole() != User.Role.TEACHER && classTeacher.getRole() != User.Role.CLASS_TEACHER) {
                throw new IllegalArgumentException("Пользователь с ID " + classDto.getClassTeacherId() + " не является учителем");
            }
        }
        
        Class classEntity = Class.builder()
                .name(classDto.getName())
                .academicYear(classDto.getAcademicYear())
                .classTeacher(classTeacher)
                .build();
        
        classEntity = classRepository.save(classEntity);
        return mapToDto(classEntity);
    }

    /**
     * Обновление информации о классе
     */
    @Transactional
    public ClassDto updateClass(Long id, ClassDto classDto) {
        log.debug("Обновление класса с ID: {}", id);
        
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Класс не найден с ID: " + id));

        if (!classEntity.getName().equals(classDto.getName()) && 
                classRepository.existsByName(classDto.getName())) {
            throw new IllegalArgumentException("Класс с таким именем уже существует");
        }

        if (classDto.getClassTeacherId() != null) {
            User classTeacher = userRepository.findById(classDto.getClassTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Учитель не найден с ID: " + classDto.getClassTeacherId()));

            if (classTeacher.getRole() != User.Role.TEACHER && classTeacher.getRole() != User.Role.CLASS_TEACHER) {
                throw new IllegalArgumentException("Пользователь с ID " + classDto.getClassTeacherId() + " не является учителем");
            }
            
            classEntity.setClassTeacher(classTeacher);
        }

        if (classDto.getName() != null) {
            classEntity.setName(classDto.getName());
        }

        if (classDto.getAcademicYear() != null) {
            classEntity.setAcademicYear(classDto.getAcademicYear());
        }
        
        classEntity = classRepository.save(classEntity);
        return mapToDto(classEntity);
    }

    /**
     * Удаление класса
     */
    @Transactional
    public void deleteClass(Long id) {
        log.debug("Удаление класса с ID: {}", id);
        
        if (!classRepository.existsById(id)) {
            throw new ResourceNotFoundException("Класс не найден с ID: " + id);
        }
        
        classRepository.deleteById(id);
    }
    
    /**
     * Преобразование сущности Class в DTO
     */
    private ClassDto mapToDto(Class classEntity) {
        ClassDto.ClassDtoBuilder builder = ClassDto.builder()
                .id(classEntity.getId())
                .name(classEntity.getName())
                .academicYear(classEntity.getAcademicYear());

        if (classEntity.getClassTeacher() != null) {
            User teacher = classEntity.getClassTeacher();
            builder.classTeacherId(teacher.getId())
                    .classTeacherName(teacher.getFirstName() + " " + teacher.getLastName());
        }

        Long studentCount = userRepository.countByRoleAndClassName(User.Role.STUDENT, classEntity.getName());
        builder.studentCount(studentCount.intValue());
        
        return builder.build();
    }
} 