package com.fivednevnik.api.service;

import com.fivednevnik.api.dto.GradeDto;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyGradeCreated(GradeDto grade) {
        
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "GRADE_CREATED");
            message.put("payload", grade);

            try {
                messagingTemplate.convertAndSend("/topic/grades", message);
            } catch (Exception e) {
            }

            try {
                messagingTemplate.convertAndSend("/topic/grades/student/" + grade.getStudentId(), message);
            } catch (Exception e) {
            }

            try {
                messagingTemplate.convertAndSend("/topic/grades/teacher/" + grade.getTeacherId(), message);
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }
    public void notifyGradeUpdated(GradeDto grade) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "GRADE_UPDATED");
            message.put("payload", grade);

            messagingTemplate.convertAndSend("/topic/grades", message);

            messagingTemplate.convertAndSend("/topic/grades/student/" + grade.getStudentId(), message);

            messagingTemplate.convertAndSend("/topic/grades/teacher/" + grade.getTeacherId(), message);

        } catch (Exception e) {
        }
    }

    public void notifyGradeDeleted(GradeDto grade) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "GRADE_DELETED");
            message.put("payload", grade);
    

            messagingTemplate.convertAndSend("/topic/grades", message);

            messagingTemplate.convertAndSend("/topic/grades/student/" + grade.getStudentId(), message);

            messagingTemplate.convertAndSend("/topic/grades/teacher/" + grade.getTeacherId(), message);

        } catch (Exception e) {
        }
    }
} 
