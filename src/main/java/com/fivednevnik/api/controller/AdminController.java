package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {

        try {
            Map<String, Object> dashboardData = new HashMap<>();
            List<UserDto> users = userService.findAllUsers();
            dashboardData.put("totalUsers", users.size());

            Map<String, Long> roleStats = new HashMap<>();
            users.forEach(user -> {
                String role = user.getRole().name();
                roleStats.put(role, roleStats.getOrDefault(role, 0L) + 1);
            });
            dashboardData.put("roleStats", roleStats);

            Map<String, String> systemInfo = new HashMap<>();
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            dashboardData.put("systemInfo", systemInfo);

            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении данных: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDto> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка пользователей: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/stats/users")
    public ResponseEntity<?> getUserStats() {
        
        try {
            List<UserDto> users = userService.findAllUsers();

            Map<String, Long> roleStats = new HashMap<>();
            users.forEach(user -> {
                String role = user.getRole().name();
                roleStats.put(role, roleStats.getOrDefault(role, 0L) + 1);
            });

            return ResponseEntity.ok(roleStats);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении статистики: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/users/recent")
    public ResponseEntity<?> getRecentUsers(@RequestParam(defaultValue = "5") int limit) {
        
        try {
            List<UserDto> allUsers = userService.findAllUsers();

            List<UserDto> recentUsers = allUsers.stream()
                    .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                    .limit(limit)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(recentUsers);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка пользователей: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
} 
