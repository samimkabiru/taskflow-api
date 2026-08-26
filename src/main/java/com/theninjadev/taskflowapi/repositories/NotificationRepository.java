package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

    List<Notification> findByRecipientIdAndIsReadFalse(UUID recipientId);

    long countByRecipientIdAndIsReadFalse(UUID recipientId);

    long deleteByRecipientId(UUID recipientId);

    // "Mark all as read" — matches the notifications panel mockup's action
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.id = :recipientId AND n.isRead = false")
    void markAllAsReadForRecipient(@Param("recipientId") UUID recipientId);
}
