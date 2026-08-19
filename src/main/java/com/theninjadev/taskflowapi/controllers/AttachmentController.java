package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.attachment.AttachmentDto;
import com.theninjadev.taskflowapi.services.AttachmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/tasks/{id}/attachments")
    public ResponseEntity<AttachmentDto> uploadAttachment(
            @PathVariable(value = "id") UUID taskId,
            @RequestParam("file") MultipartFile file
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, file, currentUserId));
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
