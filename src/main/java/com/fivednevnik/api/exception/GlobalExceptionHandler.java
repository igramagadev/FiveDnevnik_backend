package com.fivednevnik.api.exception;

import com.fivednevnik.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего приложения
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации данных
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        
        log.warn("Ошибка валидации данных: {}", errorMessage);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Ошибка валидации данных: " + errorMessage, "VALIDATION_ERROR"));
    }
    
    /**
     * Обработка ошибок отсутствия ресурса
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }
    
    /**
     * Обработка ошибок аутентификации
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        String errorCode = "AUTHENTICATION_ERROR";
        String message = "Ошибка аутентификации";
        
        if (ex instanceof BadCredentialsException) {
            message = "Неправильное имя пользователя или пароль";
            errorCode = "INVALID_CREDENTIALS";
            log.warn("Неудачная попытка входа: {}", ex.getMessage());
        } else if (ex instanceof DisabledException) {
            message = "Учетная запись отключена";
            errorCode = "ACCOUNT_DISABLED";
            log.warn("Попытка входа в отключенную учетную запись: {}", ex.getMessage());
        } else {
            log.error("Ошибка аутентификации: {}", ex.getMessage(), ex);
        }
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(message, errorCode));
    }
    
    /**
     * Обработка ошибок доступа
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Отказ в доступе: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("У вас нет прав для выполнения этой операции", "ACCESS_DENIED"));
    }
    
    /**
     * Обработка исключения NoHandlerFoundException (404 Not Found)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("Обработчик не найден: {}", ex.getMessage());
        return new ErrorResponse(
                "Запрашиваемый ресурс не найден: " + ex.getRequestURL(), 
                "RESOURCE_NOT_FOUND");
    }
    
    /**
     * Обработка исключения NoResourceFoundException (404 для статических ресурсов)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Статический ресурс не найден: {}", ex.getMessage());
        
        // Перенаправляем на корневую страницу для SPA маршрутов, если это не API запрос
        String path = ex.getResourcePath();
        if (!path.startsWith("/api/")) {
            log.info("Перенаправление клиентского маршрута на index.html: {}", path);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ErrorResponse("Перенаправление на главную страницу", "REDIRECT_TO_INDEX"));
        }
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Ресурс не найден: " + path, "RESOURCE_NOT_FOUND"));
    }

    /**
     * Обработка остальных исключений
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Внутренняя ошибка сервера", "SERVER_ERROR"));
    }
} 