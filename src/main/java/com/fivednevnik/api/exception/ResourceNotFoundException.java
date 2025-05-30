package com.fivednevnik.api.exception;

/**
 * Исключение, которое выбрасывается, когда запрашиваемый ресурс не найден
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 