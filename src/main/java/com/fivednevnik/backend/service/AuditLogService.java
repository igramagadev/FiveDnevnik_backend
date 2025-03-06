package com.fivednevnik.backend.service;

import com.fivednevnik.backend.dto.AuditLogDto;
import com.fivednevnik.backend.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void logAction(AuditLog.ActionType actionType, String entityType, Long entityId, String oldValue, String newValue, String ipAddress, String userAgent);
    Page<AuditLogDto> getAuditLogs(Pageable pageable);
    Page<AuditLogDto> getAuditLogsByUser(Long userId, Pageable pageable);
} 