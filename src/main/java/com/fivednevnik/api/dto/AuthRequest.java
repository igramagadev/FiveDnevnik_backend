package com.fivednevnik.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса авторизации
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
    
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
} 