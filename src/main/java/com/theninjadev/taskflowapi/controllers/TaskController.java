package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.task.CreateTaskRequest;
import com.theninjadev.taskflowapi.dtos.task.MoveTaskRequest;
import com.theninjadev.taskflowapi.dtos.task.TaskDto;
import com.theninjadev.taskflowapi.dtos.task.UpdateTaskRequest;
import com.theninjadev.taskflowapi.services.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/lists/{id}/tasks")
    public ResponseEntity<List<TaskDto>> getTasksForList(
            @PathVariable(value = "id") UUID taskListId
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(taskService.getTasksForList(taskListId, currentUserId));
    }

    @GetMapping("users/me/tasks")
    public ResponseEntity<List<TaskDto>> getTasksForUser() {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(taskService.getTasksForUser(currentUserId));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable(value = "id") UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(taskService.updateTask(taskId, request, currentUserId));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable(value = "id") UUID taskId
    ) {
        var currentUserId = getCurrentUserId();

        taskService.deleteTask(taskId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/tasks/{id}/move")
    public ResponseEntity<TaskDto> moveTask(
            @PathVariable(value = "id") UUID taskId,
            @Valid @RequestBody MoveTaskRequest request
    ) {
        var currentUserId = getCurrentUserId();

        return ResponseEntity.ok(taskService.moveTask(taskId, request, currentUserId));
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
