package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для администраторских функций
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    /**
     * Получение данных для панели администратора
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        log.debug("Запрос данных для панели администратора");
        
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
            
            log.info("Данные панели администратора успешно получены");
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            log.error("Ошибка при получении данных для панели администратора", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении данных: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
    
    /**
     * Получение списка всех пользователей
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        log.debug("Запрос списка всех пользователей");
        
        try {
            List<UserDto> users = userService.findAllUsers();
            log.info("Получен список пользователей, количество: {}", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении списка пользователей: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
    
    /**
     * Получение статистики пользователей по ролям
     */
    @GetMapping("/stats/users")
    public ResponseEntity<?> getUserStats() {
        log.debug("Запрос статистики пользователей");
        
        try {
            List<UserDto> users = userService.findAllUsers();

            Map<String, Long> roleStats = new HashMap<>();
            users.forEach(user -> {
                String role = user.getRole().name();
                roleStats.put(role, roleStats.getOrDefault(role, 0L) + 1);
            });
            
            log.info("Получена статистика пользователей по ролям");
            return ResponseEntity.ok(roleStats);
        } catch (Exception e) {
            log.error("Ошибка при получении статистики пользователей", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при получении статистики: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
} 