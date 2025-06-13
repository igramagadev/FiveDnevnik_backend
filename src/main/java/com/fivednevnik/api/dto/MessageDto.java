package com.fivednevnik.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long id;
    
    private Long senderId;
    
    private String senderName;
    
    @NotNull(message = "ID получателя не может быть пустым")
    private Long recipientId;
    
    private String recipientName;
    
    @NotBlank(message = "Тема не может быть пустой")
    @Size(max = 100, message = "Тема не должна превышать 100 символов")
    private String subject;
    
    @NotBlank(message = "Содержание не может быть пустым")
    private String content;
    
    private Boolean isRead;
    
    private Long replyToId;
    
    private MessageDto replyTo;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;
} 
