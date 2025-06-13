package com.fivednevnik.api.controller;

import com.fivednevnik.api.dto.MessageDto;
import com.fivednevnik.api.service.MessageService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor

public class MessageController {

    private final MessageService messageService;


    @GetMapping("/inbox")
    public ResponseEntity<List<MessageDto>> getInboxMessages(
            @RequestParam(required = false, defaultValue = "false") boolean unreadOnly) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(messageService.getInboxMessages(username, unreadOnly));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MessageDto>> getSentMessages() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(messageService.getSentMessages(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(messageService.getMessageById(id, username));
    }


    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();

        return ResponseEntity.ok(messageService.sendMessage(messageDto, senderUsername));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<MessageDto> replyToMessage(
            @PathVariable Long id,
            @Valid @RequestBody MessageDto messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();
        return ResponseEntity.ok(messageService.replyToMessage(id, messageDto, senderUsername));
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<MessageDto> markAsRead(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(messageService.markAsRead(id, username));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        messageService.deleteMessage(id, username);
        return ResponseEntity.noContent().build();
    }
} 
