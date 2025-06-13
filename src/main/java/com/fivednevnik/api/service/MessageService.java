package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.MessageDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MessageDto> getInboxMessages(String username, boolean unreadOnly) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));

        List<MessageDto> messages = getMockMessages().stream()
                .filter(msg -> user.getId().equals(msg.getRecipientId()))
                .collect(Collectors.toList());
        
        if (unreadOnly) {
            messages = messages.stream()
                    .filter(msg -> msg.getIsRead() != null && !msg.getIsRead())
                    .collect(Collectors.toList());
        }
        
        return messages;
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getSentMessages(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));

        return getMockMessages().stream()
                .filter(msg -> user.getId().equals(msg.getSenderId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto getMessageById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));

        MessageDto message = getMockMessages().stream()
                .filter(msg -> id.equals(msg.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Сообщение не найдено с ID: " + id));

        if (!user.getId().equals(message.getSenderId()) && !user.getId().equals(message.getRecipientId())) {
            throw new IllegalArgumentException("У вас нет доступа к этому сообщению");
        }

        if (user.getId().equals(message.getRecipientId()) && 
                (message.getIsRead() == null || !message.getIsRead())) {
            return markAsRead(id, username);
        }
        
        return message;
    }

    @Transactional
    public MessageDto sendMessage(MessageDto messageDto, String senderUsername) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Отправитель не найден: " + senderUsername));
        
        if (!userRepository.existsById(messageDto.getRecipientId())) {
            throw new ResourceNotFoundException("Получатель не найден с ID: " + messageDto.getRecipientId());
        }
        
        messageDto.setSenderId(sender.getId());
        messageDto.setSenderName(sender.getLastName() + " " + sender.getFirstName());
        messageDto.setIsRead(false);
        messageDto.setSentAt(LocalDateTime.now());

        messageDto.setId(1L);
        return messageDto;
    }

    @Transactional
    public MessageDto replyToMessage(Long id, MessageDto messageDto, String senderUsername) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Отправитель не найден: " + senderUsername));

        MessageDto originalMessage = getMockMessages().stream()
                .filter(msg -> id.equals(msg.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Сообщение не найдено с ID: " + id));

        if (!sender.getId().equals(originalMessage.getRecipientId())) {
            throw new IllegalArgumentException("Вы не можете ответить на это сообщение");
        }
        
        messageDto.setSenderId(sender.getId());
        messageDto.setSenderName(sender.getLastName() + " " + sender.getFirstName());
        messageDto.setRecipientId(originalMessage.getSenderId());
        messageDto.setReplyToId(id);
        messageDto.setIsRead(false);
        messageDto.setSentAt(LocalDateTime.now());
        
        if (messageDto.getSubject() == null || messageDto.getSubject().isEmpty()) {
            messageDto.setSubject("Re: " + originalMessage.getSubject());
        }

        messageDto.setId(2L);
        return messageDto;
    }

    @Transactional
    public MessageDto markAsRead(Long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));

        MessageDto message = getMockMessages().stream()
                .filter(msg -> id.equals(msg.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Сообщение не найдено с ID: " + id));

        if (!user.getId().equals(message.getRecipientId())) {
            throw new IllegalArgumentException("Вы не можете отметить это сообщение как прочитанное");
        }
        
        message.setIsRead(true);
        message.setReadAt(LocalDateTime.now());

        return message;
    }

    @Transactional
    public void deleteMessage(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));

        MessageDto message = getMockMessages().stream()
                .filter(msg -> id.equals(msg.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Сообщение не найдено с ID: " + id));

        if (!user.getId().equals(message.getSenderId()) && !user.getId().equals(message.getRecipientId())) {
            throw new IllegalArgumentException("У вас нет доступа к этому сообщению");
        }

    }

    private List<MessageDto> getMockMessages() {
        List<MessageDto> messages = new ArrayList<>();

        messages.add(MessageDto.builder()
                .id(1L)
                .senderId(2L)
                .senderName("Елена Петрова")
                .recipientId(5L)
                .recipientName("Иван Смирнов")
                .subject("ла-ла-ла")
                .content("Добрый день, Иван! Вы большой молодец!")
                .isRead(false)
                .sentAt(LocalDateTime.of(2025, 5, 28, 15, 30))
                .build());
        
        return messages;
    }
} 
