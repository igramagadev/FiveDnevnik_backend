package com.fivednevnik.api.config;

import com.fivednevnik.api.security.JwtProperties;
import com.fivednevnik.api.security.JwtService;
import com.fivednevnik.api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import lombok.RequiredArgsConstructor;

/**
 * Конфигурация JWT сервисов
 */
@Configuration
@RequiredArgsConstructor
public class JwtServiceConfig {

    private final JwtProperties jwtProperties;

    /**
     * Создает JwtService с настройками из конфигурации
     */
    @Bean("configJwtService")
    public JwtService jwtService() {
        return new JwtService(jwtProperties);
    }

    /**
     * Создает фильтр JWT аутентификации
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, JwtProperties jwtProperties) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService, jwtProperties);
    }
} 