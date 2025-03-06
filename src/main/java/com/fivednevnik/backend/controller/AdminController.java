package com.fivednevnik.backend.controller;

import com.fivednevnik.backend.dto.AuditLogDto;
import com.fivednevnik.backend.dto.SystemSettingDto;
import com.fivednevnik.backend.dto.UserDto;
import com.fivednevnik.backend.model.Role;
import com.fivednevnik.backend.service.AuditLogService;
import com.fivednevnik.backend.service.SystemSettingService;
import com.fivednevnik.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final SystemSettingService systemSettingService;
    private final AuditLogService auditLogService;

    // User management endpoints
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id, @RequestBody Role role) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserDto> updateUserStatus(@PathVariable Long id, @RequestBody boolean active) {
        return ResponseEntity.ok(userService.updateUserStatus(id, active));
    }

    // System settings endpoints
    @GetMapping("/settings")
    public ResponseEntity<List<SystemSettingDto>> getAllSettings() {
        return ResponseEntity.ok(systemSettingService.getAllSettings());
    }

    @GetMapping("/settings/{key}")
    public ResponseEntity<SystemSettingDto> getSettingByKey(@PathVariable String key) {
        return ResponseEntity.ok(systemSettingService.getSettingByKey(key));
    }

    @PutMapping("/settings/{key}")
    public ResponseEntity<SystemSettingDto> updateSetting(@PathVariable String key, @RequestBody String value) {
        return ResponseEntity.ok(systemSettingService.updateSetting(key, value));
    }

    // Audit log endpoints
    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(pageable));
    }

    @GetMapping("/audit-logs/user/{userId}")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogsByUser(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUser(userId, pageable));
    }

    // Statistics endpoints
    @GetMapping("/statistics/users")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        return ResponseEntity.ok(userService.getUserStatistics());
    }

    @GetMapping("/statistics/system")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        return ResponseEntity.ok(systemSettingService.getSystemStatistics());
    }
} 