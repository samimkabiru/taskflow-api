package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.notification.NotificationDto;
import com.theninjadev.taskflowapi.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationDto>> listForUser(Pageable pageable) {

        return ResponseEntity.ok(notificationService.listForUser(getCurrentUserId(), pageable));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable(value = "id") UUID notificationId) {

        notificationService.markAsRead(notificationId, getCurrentUserId());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead(getCurrentUserId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount(getCurrentUserId()));
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Void> clearAll() {
        notificationService.clearAll(getCurrentUserId());

        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
