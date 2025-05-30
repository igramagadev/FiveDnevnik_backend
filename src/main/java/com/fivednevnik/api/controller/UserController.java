package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Получение профиля текущего пользователя
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        log.info("Запрос профиля пользователя: {}", username);
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    /**
     * Получение всех пользователей (только для администраторов)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return ResponseEntity.ok(userService.findAllUsers());
    }

    /**
     * Получение пользователя по ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Запрос пользователя по ID: {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Обновление пользователя
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    /**
     * Удаление пользователя (только для администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение пользователей по роли (только для администраторов и классных руководителей)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public ResponseEntity<Iterable<UserDto>> getUsersByRole(@PathVariable String role) {
        log.info("Запрос пользователей с ролью: {}", role);
        // TODO: Реализовать метод в сервисе
        return ResponseEntity.ok().build();
    }

    /**
     * Получение пользователей по классу (только для администраторов и классных руководителей)
     */
    @GetMapping("/class/{className}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLASS_TEACHER')")
    public ResponseEntity<Iterable<UserDto>> getUsersByClass(@PathVariable String className) {
        log.info("Запрос пользователей класса: {}", className);
        // TODO: Реализовать метод в сервисе
        return ResponseEntity.ok().build();
    }
} 