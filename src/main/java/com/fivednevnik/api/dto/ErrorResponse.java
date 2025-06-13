package com.fivednevnik.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, Object> details;
    
    public ErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String message, String errorCode, Map<String, String> details) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();

        if (details != null) {
            this.details = new HashMap<>();
            for (Map.Entry<String, String> entry : details.entrySet()) {
                this.details.put(entry.getKey(), entry.getValue());
            }
        }
    }
} 
