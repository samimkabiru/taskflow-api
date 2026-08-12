package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.label.AssignLabelsRequest;
import com.theninjadev.taskflowapi.dtos.label.CreateLabelRequest;
import com.theninjadev.taskflowapi.dtos.label.LabelDto;
import com.theninjadev.taskflowapi.dtos.label.UpdateLabelRequest;
import com.theninjadev.taskflowapi.services.LabelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/boards/{id}/labels")
    public ResponseEntity<List<LabelDto>> getLabelsForBoard(
            @PathVariable(value = "id") UUID boardId
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(labelService.getLabelsForBoard(boardId, currentUserId));
    }

    @PatchMapping("/labels/{id}")
    public ResponseEntity<LabelDto> updateLabel(
            @PathVariable(value = "id") UUID labelId,
            @Valid @RequestBody UpdateLabelRequest request
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(labelService.updateLabel(labelId, request, currentUserId));
    }

    @DeleteMapping("/labels/{id}")
    public ResponseEntity<Void> deleteLabel(
            @PathVariable(value = "id") UUID labelId
    ) {
        var currentUserId = getCurrentUserId();

        labelService.deleteLabel(labelId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tasks/{id}/labels")
    public ResponseEntity<Void> assignLabelToTask(
            @PathVariable(value = "id") UUID taskId,
            @Valid @RequestBody AssignLabelsRequest request
    ) {
        var currentUserId = getCurrentUserId();

        labelService.assignLabelToTask(taskId, request, currentUserId);

        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
