package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.tasklist.CreateTaskListRequest;
import com.theninjadev.taskflowapi.dtos.tasklist.TaskListDto;
import com.theninjadev.taskflowapi.dtos.tasklist.UpdateTaskListRequest;
import com.theninjadev.taskflowapi.services.TaskListService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class TaskListController {

    private final TaskListService taskListService;

    @PostMapping("/boards/{id}/lists")
    public ResponseEntity<TaskListDto> createTaskList(
            @PathVariable(value = "id") UUID boardId,
            @Valid @RequestBody CreateTaskListRequest request
    ) {
        var currentUserId = getCurrentUserId();

        var taskListDto = taskListService.createTaskList(boardId, request, currentUserId);

        return ResponseEntity.ok().body(taskListDto);
    }

    @GetMapping("/boards/{id}/lists")
    public ResponseEntity<List<TaskListDto>> getTaskListsForBoard(
            @PathVariable(value = "id") UUID boardId
    ) {
        var currentUserId = getCurrentUserId();

        var taskListsDtos = taskListService.getTaskListsForBoard(boardId,  currentUserId);

        return ResponseEntity.ok(taskListsDtos);
    }

    @PatchMapping("/lists/{id}")
    public ResponseEntity<TaskListDto> updateTaskList(
            @PathVariable(value = "id") UUID listId,
            @Valid @RequestBody UpdateTaskListRequest request
    ) {
        var currentUserId = getCurrentUserId();

        var updatedTaskListDto = taskListService.updateTaskList(listId, request, currentUserId);

        return ResponseEntity.ok(updatedTaskListDto);
    }

    @DeleteMapping("/lists/{id}")
    public ResponseEntity<Void> deleteTaskList(
            @PathVariable(value = "id") UUID listId
    ) {
        var currentUserId = getCurrentUserId();

        taskListService.deleteTaskList(listId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}