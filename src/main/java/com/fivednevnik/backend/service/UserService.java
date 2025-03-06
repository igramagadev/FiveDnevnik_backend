package com.fivednevnik.backend.service;

import com.fivednevnik.backend.dto.UserDto;
import com.fivednevnik.backend.model.Role;
import com.fivednevnik.backend.model.User;
import com.fivednevnik.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    Page<UserDto> getAllUsers(Pageable pageable);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    List<UserDto> getUsersByRole(Role role);
    UserDto updateUserRole(Long id, Role role);
    UserDto updateUserStatus(Long id, boolean active);
    void changePassword(Long id, String oldPassword, String newPassword);
    Map<String, Object> getUserStatistics();
    UserDto convertToDto(User user);
} 