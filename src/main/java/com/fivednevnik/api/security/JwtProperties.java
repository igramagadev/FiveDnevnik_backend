package com.fivednevnik.api.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Настройки JWT токена
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtProperties {

    /**
     * Секретный ключ для подписи JWT
     */
    private String secret = "2ydgr7d8FD5f7mDYzWXXqwerty123456789ASDFGHJKL";
    
    /**
     * Срок действия токена в миллисекундах (24 часа по умолчанию)
     */
    private long expirationMs = 86400000;
    
    /**
     * Префикс для заголовка Authorization
     */
    private String tokenPrefix = "Bearer ";
    
    /**
     * Имя заголовка для токена
     */
    private String headerName = "Authorization";
} 