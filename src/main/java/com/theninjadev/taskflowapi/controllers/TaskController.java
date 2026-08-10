package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.task.CreateTaskRequest;
import com.theninjadev.taskflowapi.dtos.task.TaskDto;
import com.theninjadev.taskflowapi.services.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("lists/{id}/tasks")
    public ResponseEntity<TaskDto> createTask(
            @PathVariable(value = "id") UUID taskListId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        var currentUserId = getCurrentUserId();

        var taskDto = taskService.createTask(taskListId, request, currentUserId);

        return ResponseEntity.ok(taskDto);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTask(
            @PathVariable(value = "id") UUID taskId
    ) {
        var currentUserId = getCurrentUserId();

        var taskDto = taskService.getTask(taskId, currentUserId);

        return ResponseEntity.ok(taskDto);
    }

    @GetMapping("/boards/{id}/tasks")
    public ResponseEntity<Page<TaskDto>> getTasksForBoard(
            @PathVariable(value = "id") UUID boardId,
            Pageable pageable
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(taskService.getTasksForBoard(boardId, pageable, currentUserId));
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
