package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.AuthRequest;
import com.fivednevnik.api.dto.AuthResponse;
import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.UserRepository;
import com.fivednevnik.api.security.JwtService;
import com.fivednevnik.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Контроллер для авторизации пользователей
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Авторизация пользователя
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Попытка авторизации пользователя: {}", authRequest.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(authRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.debug("Найден пользователь: {}, роль: {}", user.getUsername(), user.getRole());
            log.debug("Хешированный пароль в БД: {}", user.getPassword());
            log.debug("Пароль соответствует введенному: {}", 
                     passwordEncoder.matches(authRequest.getPassword(), user.getPassword()));
        } else {
            log.warn("Пользователь не найден: {}", authRequest.getUsername());
        }
        
        try {
            log.debug("Аутентификация пользователя с именем: {}", authRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            
            log.info("Пользователь успешно аутентифицирован: {}", authRequest.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("Генерация JWT токена для пользователя: {}", authRequest.getUsername());
            String jwt = jwtService.generateToken(authentication);

            log.debug("Получение данных пользователя: {}", authRequest.getUsername());
            UserDto userDto = userService.findByUsername(authRequest.getUsername());
            
            log.info("Успешный вход пользователя: {}, роль: {}", authRequest.getUsername(), userDto.getRole());
            return ResponseEntity.ok(new AuthResponse(jwt, userDto));
        } catch (BadCredentialsException e) {
            log.warn("Неправильное имя пользователя или пароль: {}", authRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Неправильное имя пользователя или пароль", "INVALID_CREDENTIALS"));
        } catch (DisabledException e) {
            log.warn("Учетная запись пользователя отключена: {}", authRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Учетная запись отключена", "ACCOUNT_DISABLED"));
        } catch (AuthenticationException e) {
            log.error("Ошибка аутентификации пользователя: {}", authRequest.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Ошибка аутентификации: " + e.getMessage(), "AUTHENTICATION_ERROR"));
        } catch (Exception e) {
            log.error("Внутренняя ошибка сервера при входе пользователя: {}", authRequest.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Внутренняя ошибка сервера: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
    
    /**
     * Регистрация нового пользователя (доступно только администраторам)
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        log.info("Регистрация нового пользователя администратором: {}", userDto.getUsername());
        
        try {
            UserDto createdUser = userService.createUser(userDto);
            log.info("Пользователь успешно создан администратором: {}, роль: {}", 
                     createdUser.getUsername(), createdUser.getRole());
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации при создании пользователя: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", userDto.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при создании пользователя: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    /**
     * Публичная регистрация нового пользователя (ученик по умолчанию)
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) {
        log.info("Публичная регистрация нового пользователя: {}", userDto.getUsername());
        
        try {

            userDto.setRole(User.Role.STUDENT);

            log.debug("Создание нового пользователя с ролью STUDENT: {}", userDto.getUsername());
            UserDto createdUser = userService.createUser(userDto);

            log.debug("Аутентификация нового пользователя: {}", userDto.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUsername(),
                            userDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateToken(authentication);
            
            log.info("Пользователь успешно зарегистрирован и аутентифицирован: {}", userDto.getUsername());
            return ResponseEntity.ok(new AuthResponse(jwt, createdUser));
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации при самостоятельной регистрации: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Ошибка при самостоятельной регистрации: {}", userDto.getUsername(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при регистрации: " + e.getMessage(), "SERVER_ERROR"));
        }
    }
    
    /**
     * Проверка статуса аутентификации
     */
    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Проверка статуса аутентификации. Аутентифицирован: {}", 
                 authentication != null && authentication.isAuthenticated());
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            try {
                String username = authentication.getName();
                UserDto userDto = userService.findByUsername(username);
                log.info("Пользователь аутентифицирован: {}, роль: {}", username, userDto.getRole());
                return ResponseEntity.ok(userDto);
            } catch (Exception e) {
                log.error("Ошибка при получении данных пользователя", e);
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Ошибка при получении данных пользователя", "SERVER_ERROR"));
            }
        }
        
        log.info("Пользователь не аутентифицирован");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Пользователь не аутентифицирован", "NOT_AUTHENTICATED"));
    }
} 