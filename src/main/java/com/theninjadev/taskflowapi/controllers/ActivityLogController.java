package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.activitylog.ActivityLogDto;
import com.theninjadev.taskflowapi.services.ActivityLogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/boards/{id}/activity")
    public ResponseEntity<Page<ActivityLogDto>> listForBoard(
            @PathVariable(value = "id") UUID boardId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(activityLogService.listForBoard(boardId, getCurrentUserId(), pageable));
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
