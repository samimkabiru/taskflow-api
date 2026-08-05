package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.board.BoardMemberDto;
import com.theninjadev.taskflowapi.services.BoardMembershipService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
@AllArgsConstructor
public class BoardMemberController {

    private final BoardMembershipService boardMembershipService;

    @GetMapping("{id}/members")
    public ResponseEntity<List<BoardMemberDto>> getBoardMembers(
            @PathVariable(value = "id") UUID boardId
    ) {
        var currentUserId = getCurrentUserId();

        var boardMembersDtos = boardMembershipService.getBoardMembers(boardId, currentUserId);

        return ResponseEntity.ok(boardMembersDtos);
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
