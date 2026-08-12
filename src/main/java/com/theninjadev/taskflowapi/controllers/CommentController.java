package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.comment.CommentDto;
import com.theninjadev.taskflowapi.dtos.comment.CreateCommentRequest;
import com.theninjadev.taskflowapi.services.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
