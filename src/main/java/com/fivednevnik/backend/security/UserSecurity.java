package com.fivednevnik.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        org.springframework.security.core.userdetails.User principal = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        // Здесь нужно получить ID пользователя из его имени
        // В реальном приложении это может быть реализовано через UserService
        // Для простоты предположим, что имя пользователя содержит его ID
        return principal.getUsername().equals(userId.toString());
    }
} 