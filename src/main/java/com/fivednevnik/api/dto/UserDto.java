package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fivednevnik.api.model.User;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 50, message = "Имя должно содержать не более 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(max = 50, message = "Фамилия должна содержать не более 50 символов")
    private String lastName;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат Email")
    private String email;

    private User.Role role;
    private String className;
    private String schoolName;
    private String phone;
    private Long createdAt;
    private String avatarUrl;

    @JsonIgnore
    private String fullName;
    
    @JsonIgnore
    private String initials;

    public String getFullName() {
        return lastName + " " + firstName;
    }

    public String getInitials() {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            return "";
        }
        return String.valueOf(lastName.charAt(0)) + String.valueOf(firstName.charAt(0));
    }
} 
