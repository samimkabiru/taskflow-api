package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.attachment.AttachmentDto;
import com.theninjadev.taskflowapi.services.AttachmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

    @GetMapping("/tasks/{id}/attachments")
    public ResponseEntity<List<AttachmentDto>> getAttachmentsForTask(
            @PathVariable(value = "id") UUID taskId
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(attachmentService.getAttachmentsForTask(taskId, currentUserId));
    }

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<byte[]> downloadAttachment(
            @PathVariable(value = "id") UUID attachmentId
    ) {
        var currentUserId = getCurrentUserId();

        var result = attachmentService.downloadAttachment(attachmentId, currentUserId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.fileName() + "\"")
                .body(result.fileBytes());
    }

    @DeleteMapping("/attachments/{id}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable(value = "id") UUID attachmentId
    ) {
        var currentUserId = getCurrentUserId();

        attachmentService.deleteAttachment(attachmentId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
