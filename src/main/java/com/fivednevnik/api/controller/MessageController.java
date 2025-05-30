package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.MessageDto;
import com.fivednevnik.api.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с сообщениями
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    /**
     * Получение входящих сообщений для текущего пользователя
     */
    @GetMapping("/inbox")
    public ResponseEntity<List<MessageDto>> getInboxMessages(
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Запрос входящих сообщений для пользователя: {} (только непрочитанные: {})", username, unreadOnly);
        return ResponseEntity.ok(messageService.getInboxMessages(username, unreadOnly));
    }

    /**
     * Получение отправленных сообщений для текущего пользователя
     */
    @GetMapping("/sent")
    public ResponseEntity<List<MessageDto>> getSentMessages() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Запрос отправленных сообщений для пользователя: {}", username);
        return ResponseEntity.ok(messageService.getSentMessages(username));
    }

    /**
     * Получение сообщения по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Запрос сообщения с ID {} пользователем {}", id, username);
        return ResponseEntity.ok(messageService.getMessageById(id, username));
    }

    /**
     * Отправка нового сообщения
     */
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();
        log.info("Отправка сообщения от пользователя {}: {}", senderUsername, messageDto);
        return ResponseEntity.ok(messageService.sendMessage(messageDto, senderUsername));
    }

    /**
     * Ответ на сообщение
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<MessageDto> replyToMessage(
            @PathVariable Long id,
            @Valid @RequestBody MessageDto messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();
        log.info("Ответ на сообщение с ID {} от пользователя {}", id, senderUsername);
        return ResponseEntity.ok(messageService.replyToMessage(id, messageDto, senderUsername));
    }

    /**
     * Отметить сообщение как прочитанное
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<MessageDto> markAsRead(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Отметка сообщения с ID {} как прочитанное пользователем {}", id, username);
        return ResponseEntity.ok(messageService.markAsRead(id, username));
    }

    /**
     * Удаление сообщения
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Удаление сообщения с ID {} пользователем {}", id, username);
        messageService.deleteMessage(id, username);
        return ResponseEntity.noContent().build();
    }
} 