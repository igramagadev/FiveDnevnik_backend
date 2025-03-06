package com.fivednevnik.backend.service.impl;

import com.fivednevnik.backend.dto.SystemSettingDto;
import com.fivednevnik.backend.dto.UserDto;
import com.fivednevnik.backend.model.SystemSetting;
import com.fivednevnik.backend.model.User;
import com.fivednevnik.backend.repository.SystemSettingRepository;
import com.fivednevnik.backend.security.SecurityUtils;
import com.fivednevnik.backend.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingDto> getAllSettings() {
        return systemSettingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SystemSettingDto getSettingByKey(String key) {
        SystemSetting setting = systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found with key: " + key));
        return convertToDto(setting);
    }

    @Override
    @Transactional
    public SystemSettingDto updateSetting(String key, String value) {
        SystemSetting setting = systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found with key: " + key));
        
        if (setting.isSystem()) {
            throw new RuntimeException("Cannot modify system setting");
        }
        
        setting.setValue(value);
        setting.setModifiedBy(securityUtils.getCurrentUser());
        
        return convertToDto(systemSettingRepository.save(setting));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Здесь можно добавить различные статистические данные о системе
        statistics.put("settingsCount", systemSettingRepository.count());
        
        return statistics;
    }

    private SystemSettingDto convertToDto(SystemSetting setting) {
        return SystemSettingDto.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .description(setting.getDescription())
                .type(setting.getType())
                .isSystem(setting.isSystem())
                .modifiedBy(setting.getModifiedBy() != null ? convertUserToDto(setting.getModifiedBy()) : null)
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }

    private UserDto convertUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
} 