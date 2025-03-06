package com.fivednevnik.backend.dto;

import com.fivednevnik.backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 20, message = "Имя пользователя должно содержать от 3 до 20 символов")
    private String username;
    
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 40, message = "Пароль должен содержать от 6 до 40 символов")
    private String password;
    
    @NotBlank(message = "ФИО не может быть пустым")
    private String fullName;
    
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;
    
    @NotNull(message = "Роль не может быть пустой")
    private Role role;
}