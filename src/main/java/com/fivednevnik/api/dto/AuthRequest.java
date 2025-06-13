package com.fivednevnik.api.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
    
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
} 
