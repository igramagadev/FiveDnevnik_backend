package com.fivednevnik.api.service.impl;

import com.fivednevnik.api.dto.SubjectDto;
import com.fivednevnik.api.model.Subject;
import com.fivednevnik.api.repository.SubjectRepository;
import com.fivednevnik.api.service.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с предметами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    /**
     * Получить все предметы
     */
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> getAllSubjects() {
        log.debug("Получение списка всех предметов");
        return subjectRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить предмет по ID
     */
    @Override
    @Transactional(readOnly = true)
    public SubjectDto getSubjectById(Long id) {
        log.debug("Получение предмета с ID: {}", id);
        return subjectRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с ID " + id + " не найден"));
    }

    /**
     * Создать новый предмет
     */
    @Override
    @Transactional
    public SubjectDto createSubject(SubjectDto subjectDto) {
        log.debug("Создание нового предмета: {}", subjectDto.getName());
        
        if (subjectRepository.existsByName(subjectDto.getName())) {
            throw new IllegalArgumentException("Предмет с именем " + subjectDto.getName() + " уже существует");
        }
        
        Subject subject = mapToEntity(subjectDto);
        Subject savedSubject = subjectRepository.save(subject);
        
        return mapToDto(savedSubject);
    }

    /**
     * Обновить существующий предмет
     */
    @Override
    @Transactional
    public SubjectDto updateSubject(Long id, SubjectDto subjectDto) {
        log.debug("Обновление предмета с ID: {}", id);
        
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с ID " + id + " не найден"));

        if (!subject.getName().equals(subjectDto.getName()) && 
            subjectRepository.existsByName(subjectDto.getName())) {
            throw new IllegalArgumentException("Предмет с именем " + subjectDto.getName() + " уже существует");
        }

        subject.setName(subjectDto.getName());
        subject.setDescription(subjectDto.getDescription());
        if (subjectDto.getActive() != null) {
            subject.setActive(subjectDto.getActive());
        }
        
        Subject updatedSubject = subjectRepository.save(subject);
        return mapToDto(updatedSubject);
    }

    /**
     * Удалить предмет
     */
    @Override
    @Transactional
    public void deleteSubject(Long id) {
        log.debug("Удаление предмета с ID: {}", id);
        
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Предмет с ID " + id + " не найден");
        }
        
        subjectRepository.deleteById(id);
    }
    
    /**
     * Преобразование сущности в DTO
     */
    private SubjectDto mapToDto(Subject subject) {
        return SubjectDto.builder()
                .id(subject.getId())
                .name(subject.getName())
                .description(subject.getDescription())
                .active(subject.getActive())
                .build();
    }
    
    /**
     * Преобразование DTO в сущность
     */
    private Subject mapToEntity(SubjectDto dto) {
        return Subject.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }
} 