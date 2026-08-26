package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.notification.NotificationDto;
import com.theninjadev.taskflowapi.entities.Notification;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.NotificationType;
import com.theninjadev.taskflowapi.exceptions.NotYourNotificationException;
import com.theninjadev.taskflowapi.exceptions.NotificationNotFoundException;
import com.theninjadev.taskflowapi.mappers.NotificationMapper;
import com.theninjadev.taskflowapi.repositories.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public void notify(NotificationType type, User recipient, Map<String, Object> payload) {
        var notification = new Notification();

        notification.setType(type);
        notification.setRecipient(recipient);
        notification.setPayload(payload);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public Page<NotificationDto> listForUser(UUID currentUserId, Pageable pageable) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUserId, pageable)
                .map(notificationMapper::toDto);
    }

    public void markAsRead(UUID notificationId, UUID currentUserId) {
        var notification = notificationRepository.findById(notificationId).orElseThrow(NotificationNotFoundException::new);

        if (!notification.getRecipient().getId().equals(currentUserId))
            throw new NotYourNotificationException();

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID currentUserId) {
        notificationRepository.markAllAsReadForRecipient(currentUserId);
    }

    public Long getUnreadCount(UUID currentUserId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUserId);
    }

    @Transactional
    public void clearAll(UUID currentUserId) {
        notificationRepository.deleteByRecipientId(currentUserId);
    }
}
