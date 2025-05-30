package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.MessageDto;
import com.fivednevnik.api.exception.ResourceNotFoundException;
import com.fivednevnik.api.model.User;
import com.fivednevnik.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с сообщениями
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final UserRepository userRepository;

    /**
     * Получение входящих сообщений для пользователя
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getInboxMessages(String username, boolean unreadOnly) {
        log.debug("Получение входящих сообщений для пользователя: {} (только непрочитанные: {})", username, unreadOnly);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        // TODO: Реализовать получение сообщений из репозитория
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
    
    /**
     * Получение отправленных сообщений для пользователя
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getSentMessages(String username) {
        log.debug("Получение отправленных сообщений для пользователя: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        // TODO: Реализовать получение сообщений из репозитория
        return getMockMessages().stream()
                .filter(msg -> user.getId().equals(msg.getSenderId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Получение сообщения по ID
     */
    @Transactional
    public MessageDto getMessageById(Long id, String username) {
        log.debug("Получение сообщения с ID {} пользователем {}", id, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        // TODO: Реализовать получение сообщения из репозитория
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
    
    /**
     * Отправка нового сообщения
     */
    @Transactional
    public MessageDto sendMessage(MessageDto messageDto, String senderUsername) {
        log.debug("Отправка сообщения от пользователя {}: {}", senderUsername, messageDto);
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Отправитель не найден: " + senderUsername));
        
        if (!userRepository.existsById(messageDto.getRecipientId())) {
            throw new ResourceNotFoundException("Получатель не найден с ID: " + messageDto.getRecipientId());
        }
        
        messageDto.setSenderId(sender.getId());
        messageDto.setSenderName(sender.getLastName() + " " + sender.getFirstName());
        messageDto.setIsRead(false);
        messageDto.setSentAt(LocalDateTime.now());
        
        // TODO: Реализовать сохранение сообщения в репозиторий
        messageDto.setId(1L); // Временный ID для мока
        return messageDto;
    }
    
    /**
     * Ответ на сообщение
     */
    @Transactional
    public MessageDto replyToMessage(Long id, MessageDto messageDto, String senderUsername) {
        log.debug("Ответ на сообщение с ID {} от пользователя {}", id, senderUsername);
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Отправитель не найден: " + senderUsername));
        
        // TODO: Реализовать получение оригинального сообщения из репозитория
        MessageDto originalMessage = getMockMessages().stream()
                .filter(msg -> id.equals(msg.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Сообщение не найдено с ID: " + id));
        
        // Проверяем, что пользователь является получателем оригинального сообщения
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
        
        // TODO: Реализовать сохранение сообщения в репозиторий
        messageDto.setId(2L); // Временный ID для мока
        return messageDto;
    }
    
    /**
     * Отметить сообщение как прочитанное
     */
    @Transactional
    public MessageDto markAsRead(Long id, String username) {
        log.debug("Отметка сообщения с ID {} как прочитанное пользователем {}", id, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        // TODO: Реализовать получение сообщения из репозитория
        MessageDto message = getMockMessages().stream()
                .filter(msg -> id.equals(msg.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Сообщение не найдено с ID: " + id));
        
        // Проверяем, что пользователь является получателем
        if (!user.getId().equals(message.getRecipientId())) {
            throw new IllegalArgumentException("Вы не можете отметить это сообщение как прочитанное");
        }
        
        message.setIsRead(true);
        message.setReadAt(LocalDateTime.now());
        
        // TODO: Реализовать сохранение сообщения в репозиторий
        return message;
    }
    
    /**
     * Удаление сообщения
     */
    @Transactional
    public void deleteMessage(Long id, String username) {
        log.debug("Удаление сообщения с ID {} пользователем {}", id, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        
        // TODO: Реализовать получение сообщения из репозитория
        MessageDto message = getMockMessages().stream()
                .filter(msg -> id.equals(msg.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Сообщение не найдено с ID: " + id));
        
        // Проверяем, что пользователь является отправителем или получателем
        if (!user.getId().equals(message.getSenderId()) && !user.getId().equals(message.getRecipientId())) {
            throw new IllegalArgumentException("У вас нет доступа к этому сообщению");
        }
        
        // TODO: Реализовать удаление сообщения из репозитория
    }
    
    /**
     * Временный метод для создания тестовых данных
     */
    private List<MessageDto> getMockMessages() {
        List<MessageDto> messages = new ArrayList<>();
        
        // Сообщение 1: От учителя к ученику
        messages.add(MessageDto.builder()
                .id(1L)
                .senderId(2L)
                .senderName("Елена Петрова")
                .recipientId(5L)
                .recipientName("Иван Смирнов")
                .subject("Домашнее задание на следующую неделю")
                .content("Добрый день, Иван! Напоминаю, что на следующей неделе будет контрольная работа по теме \"Квадратные уравнения\". Пожалуйста, повторите материал.")
                .isRead(false)
                .sentAt(LocalDateTime.of(2024, 5, 28, 15, 30))
                .build());
        
        // Сообщение 2: От ученика к учителю
        messages.add(MessageDto.builder()
                .id(2L)
                .senderId(5L)
                .senderName("Иван Смирнов")
                .recipientId(2L)
                .recipientName("Елена Петрова")
                .subject("Вопрос по домашнему заданию")
                .content("Здравствуйте, Елена Викторовна! У меня возник вопрос по заданию №130. Могли бы вы объяснить, как решать такой тип уравнений?")
                .isRead(true)
                .sentAt(LocalDateTime.of(2024, 5, 28, 18, 45))
                .readAt(LocalDateTime.of(2024, 5, 28, 19, 30))
                .build());
        
        // Сообщение 3: От родителя к классному руководителю
        messages.add(MessageDto.builder()
                .id(3L)
                .senderId(8L)
                .senderName("Ольга Смирнова")
                .recipientId(3L)
                .recipientName("Сергей Иванов")
                .subject("Пропуск занятий")
                .content("Здравствуйте, Сергей Петрович! Сообщаю, что мой сын Иван не сможет присутствовать на занятиях 3-5 июня по причине болезни. Справку предоставим по возвращении.")
                .isRead(false)
                .sentAt(LocalDateTime.of(2024, 5, 29, 10, 15))
                .build());
        
        return messages;
    }
} 