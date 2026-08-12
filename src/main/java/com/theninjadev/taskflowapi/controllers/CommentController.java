package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.comment.CommentDto;
import com.theninjadev.taskflowapi.dtos.comment.CreateCommentRequest;
import com.theninjadev.taskflowapi.services.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/tasks/{id}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable(value = "id") UUID taskId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(commentService.createComment(taskId, request, currentUserId));
    }

    @GetMapping("/tasks/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsForTask(
            @PathVariable(value = "id") UUID taskId
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(commentService.getCommentsForTask(taskId, currentUserId));
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
