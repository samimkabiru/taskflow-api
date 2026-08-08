package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.tasklist.CreateTaskListRequest;
import com.theninjadev.taskflowapi.dtos.tasklist.TaskListDto;
import com.theninjadev.taskflowapi.services.TaskListService;
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

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}