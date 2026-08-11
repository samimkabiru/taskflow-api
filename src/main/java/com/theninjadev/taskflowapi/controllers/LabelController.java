package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.label.CreateLabelRequest;
import com.theninjadev.taskflowapi.dtos.label.LabelDto;
import com.theninjadev.taskflowapi.services.LabelService;
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
public class LabelController {
    private final LabelService labelService;

    @PostMapping("/boards/{id}/labels")
    public ResponseEntity<LabelDto> createLabel(
            @PathVariable(value = "id") UUID boardId,
            @Valid @RequestBody CreateLabelRequest request
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(labelService.createLabel(boardId, request, currentUserId));
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
