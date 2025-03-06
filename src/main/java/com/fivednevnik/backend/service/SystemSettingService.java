package com.fivednevnik.backend.service;

import com.fivednevnik.backend.dto.SystemSettingDto;

import java.util.List;
import java.util.Map;

public interface SystemSettingService {
    List<SystemSettingDto> getAllSettings();
    SystemSettingDto getSettingByKey(String key);
    SystemSettingDto updateSetting(String key, String value);
    Map<String, Object> getSystemStatistics();
} 