package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.AcademicPeriodDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.AcademicPeriod;
import com.fivednevnik.api.repository.AcademicPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AcademicPeriodService {

    private final AcademicPeriodRepository academicPeriodRepository;

    @Transactional(readOnly = true)
    public List<AcademicPeriodDto> getAllPeriods() {
        return academicPeriodRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AcademicPeriodDto getPeriodById(Long id) {
        AcademicPeriod period = academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Учебный период не найден с ID: " + id));
        return mapToDto(period);
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriodDto> getPeriodsByType(String type) {
        return academicPeriodRepository.findByType(type).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriodDto> getPeriodsByAcademicYear(String academicYear) {
        return academicPeriodRepository.findByAcademicYear(academicYear).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriodDto> getPeriodsByTypeAndAcademicYear(String type, String academicYear) {
        return academicPeriodRepository.findByTypeAndAcademicYear(type, academicYear).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AcademicPeriodDto getCurrentPeriod() {
        AcademicPeriod period = academicPeriodRepository.findByIsCurrentTrue()
                .orElseGet(() -> {
                    LocalDate today = LocalDate.now();
                    return academicPeriodRepository.findByDate(today).stream()
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Текущий учебный период не найден"));
                });
        return mapToDto(period);
    }

    @Transactional(readOnly = true)
    public List<AcademicPeriodDto> getPeriodsByDate(LocalDate date) {
        return academicPeriodRepository.findByDate(date).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AcademicPeriodDto getPeriodByDateAndType(LocalDate date, String type) {
        AcademicPeriod period = academicPeriodRepository.findByDateAndType(date, type)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Учебный период не найден для даты: " + date + " и типа: " + type));
        return mapToDto(period);
    }

    @Transactional
    public AcademicPeriodDto createPeriod(AcademicPeriodDto periodDto) {
        if (periodDto.isCurrent()) {
            academicPeriodRepository.findByIsCurrentTrue()
                    .ifPresent(currentPeriod -> {
                        currentPeriod.setCurrent(false);
                        academicPeriodRepository.save(currentPeriod);
                    });
        }
        
        AcademicPeriod period = mapToEntity(periodDto);
        AcademicPeriod savedPeriod = academicPeriodRepository.save(period);
        return mapToDto(savedPeriod);
    }

    @Transactional
    public AcademicPeriodDto updatePeriod(Long id, AcademicPeriodDto periodDto) {
        
        AcademicPeriod period = academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Учебный период не найден с ID: " + id));
        if (periodDto.isCurrent() && !period.isCurrent()) {
            academicPeriodRepository.findByIsCurrentTrue()
                    .ifPresent(currentPeriod -> {
                        currentPeriod.setCurrent(false);
                        academicPeriodRepository.save(currentPeriod);
                    });
        }

        period.setName(periodDto.getName());
        period.setType(periodDto.getType());
        period.setStartDate(periodDto.getStartDate());
        period.setEndDate(periodDto.getEndDate());
        period.setAcademicYear(periodDto.getAcademicYear());
        period.setOrderNumber(periodDto.getOrderNumber());
        period.setCurrent(periodDto.isCurrent());
        
        AcademicPeriod updatedPeriod = academicPeriodRepository.save(period);
        return mapToDto(updatedPeriod);
    }

    @Transactional
    public void deletePeriod(Long id) {
        
        if (!academicPeriodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Учебный период не найден с ID: " + id);
        }
        
        academicPeriodRepository.deleteById(id);
    }

    @Transactional
    public AcademicPeriodDto setCurrentPeriod(Long id) {

        academicPeriodRepository.findByIsCurrentTrue()
                .ifPresent(currentPeriod -> {
                    currentPeriod.setCurrent(false);
                    academicPeriodRepository.save(currentPeriod);
                });

        AcademicPeriod period = academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Учебный период не найден с ID: " + id));
        
        period.setCurrent(true);
        AcademicPeriod updatedPeriod = academicPeriodRepository.save(period);
        return mapToDto(updatedPeriod);
    }

    private AcademicPeriodDto mapToDto(AcademicPeriod period) {
        return AcademicPeriodDto.builder()
                .id(period.getId())
                .name(period.getName())
                .type(period.getType())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .academicYear(period.getAcademicYear())
                .orderNumber(period.getOrderNumber())
                .isCurrent(period.isCurrent())
                .build();
    }

    private AcademicPeriod mapToEntity(AcademicPeriodDto dto) {
        return AcademicPeriod.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .academicYear(dto.getAcademicYear())
                .orderNumber(dto.getOrderNumber())
                .isCurrent(dto.isCurrent())
                .build();
    }
} 