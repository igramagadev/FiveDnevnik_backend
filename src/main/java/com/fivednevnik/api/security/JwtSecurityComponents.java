package com.fivednevnik.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация компонентов JWT
 */
@Configuration
@RequiredArgsConstructor
public class JwtSecurityComponents {

    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    /**
     * Настройка кодировщика паролей
     */
    @Bean("jwtPasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настройка менеджера аутентификации
     */
    @Bean("jwtAuthenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
} 