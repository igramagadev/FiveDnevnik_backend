package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.Class;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.ClassRepository;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        return mapToDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .className(userDto.getClassName())
                .schoolName(userDto.getSchoolName())
                .phone(userDto.getPhone())
                .avatarUrl(userDto.getAvatarUrl())
                .build();
        
        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));
        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findUsersByRoleAndClassId(User.Role role, Long classId) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Класс не найден с ID: " + classId));

        List<User> users = userRepository.findByRoleAndClassId(role, classId);
        if (users.isEmpty()) {
            users = userRepository.findByRoleAndClassName(role, classEntity.getName());
        }
        
        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDto> findUsersByRoleAndClassName(User.Role role, String className) {

        List<User> users = userRepository.findByRoleAndClassName(role, className);
        
        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));

        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        
        if (userDto.getEmail() != null) {
            if (!user.getEmail().equals(userDto.getEmail()) && 
                    userRepository.existsByEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("Email уже используется другим пользователем");
            }
            user.setEmail(userDto.getEmail());
        }
        
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
        
        if (userDto.getClassName() != null) {
            user.setClassName(userDto.getClassName());
        }
        
        if (userDto.getSchoolName() != null) {
            user.setSchoolName(userDto.getSchoolName());
        }
        
        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }
        
        if (userDto.getAvatarUrl() != null) {
            user.setAvatarUrl(userDto.getAvatarUrl());
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        user = userRepository.save(user);
        return mapToDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователь не найден с ID: " + id);
        }
        
        userRepository.deleteById(id);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .className(user.getClassName())
                .schoolName(user.getSchoolName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
} 
