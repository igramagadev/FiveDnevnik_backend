package com.fivednevnik.backend.dto;

import com.fivednevnik.backend.model.SystemSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingDto {
    private Long id;
    private String key;
    private String value;
    private String description;
    private SystemSetting.SettingType type;
    private boolean isSystem;
    private UserDto modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 