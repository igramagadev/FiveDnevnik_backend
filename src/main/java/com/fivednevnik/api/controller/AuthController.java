package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.AuthRequest;
import com.fivednevnik.api.dto.AuthResponse;
import com.fivednevnik.api.dto.ErrorResponse;
import com.fivednevnik.api.dto.UserDto;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.UserRepository;
import com.fivednevnik.api.security.JwtProperties;
import com.fivednevnik.api.security.JwtService;
import com.fivednevnik.api.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {

        Optional<User> userOpt = userRepository.findByUsername(authRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String currentAlgorithm = jwtProperties.getAlgorithm();
            if (!"HS256".equalsIgnoreCase(currentAlgorithm)) {
                jwtProperties.setAlgorithm("HS256");
            }
            
            String jwt = jwtService.generateToken(authentication);
            UserDto userDto = userService.findByUsername(authRequest.getUsername());

            AuthResponse authResponse = new AuthResponse(jwt, userDto);

            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Неправильное имя пользователя или пароль", "INVALID_CREDENTIALS"));
        } catch (DisabledException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Учетная запись отключена", "ACCOUNT_DISABLED"));
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Ошибка аутентификации: " + e.getMessage(), "AUTHENTICATION_ERROR"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Внутренняя ошибка сервера: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {

        try {
            UserDto createdUser = userService.createUser(userDto);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при создании пользователя: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) {

        try {

            userDto.setRole(User.Role.STUDENT);

            UserDto createdUser = userService.createUser(userDto);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUsername(),
                            userDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateToken(authentication);

            return ResponseEntity.ok(new AuthResponse(jwt, createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ошибка при регистрации: " + e.getMessage(), "SERVER_ERROR"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            try {
                String username = authentication.getName();
                UserDto userDto = userService.findByUsername(username);
                return ResponseEntity.ok(userDto);
            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Ошибка при получении данных пользователя", "SERVER_ERROR"));
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Пользователь не аутентифицирован", "NOT_AUTHENTICATED"));
    }

    @GetMapping("/reset-tokens")
    public ResponseEntity<?> resetTokens() {
        return ResponseEntity.ok(new ErrorResponse(
                "Необходимо выполнить повторный вход.",
                "TOKENS_RESET"));
    }

}
