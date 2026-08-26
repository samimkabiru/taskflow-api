package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.entities.Notification;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.NotificationType;
import com.theninjadev.taskflowapi.repositories.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void notify(NotificationType type, User recipient, Map<String, Object> payload) {
        var notification = new Notification();

        notification.setType(type);
        notification.setRecipient(recipient);
        notification.setPayload(payload);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }
}
