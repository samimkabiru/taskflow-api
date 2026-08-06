package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.board.BoardInviteDto;
import com.theninjadev.taskflowapi.dtos.board.BoardMemberDto;
import com.theninjadev.taskflowapi.dtos.board.InviteMemberRequest;
import com.theninjadev.taskflowapi.dtos.board.UpdateMemberRoleRequest;
import com.theninjadev.taskflowapi.services.BoardMembershipService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("{id}/invites")
    public ResponseEntity<BoardInviteDto> inviteMember(
            @PathVariable(value = "id") UUID boardId,
            @Valid @RequestBody InviteMemberRequest request
    ) {
        var currentUserId = getCurrentUserId();

        var boardInvite = boardMembershipService.inviteMember(boardId, request, currentUserId);

        return ResponseEntity.ok(boardInvite);
    }


    @PostMapping("/invites/{id}/accept")
    public ResponseEntity<BoardMemberDto> acceptInvite(
            @PathVariable(value = "id") UUID inviteId
    ) {
        var currentUserId = getCurrentUserId();

        var boardMemberDto = boardMembershipService.acceptInvite(inviteId, currentUserId);

        return ResponseEntity.ok(boardMemberDto);
    }

    @PatchMapping("/{boardId}/members/{userId}")
    public ResponseEntity<BoardMemberDto> updateMemberRole(
            @PathVariable UUID boardId,
            @PathVariable(value = "userId") UUID targetUserId,
            @Valid @RequestBody UpdateMemberRoleRequest request
            ) {
        var currentUserId = getCurrentUserId();

        var boardMemberDto = boardMembershipService.updateMemberRole(boardId, targetUserId, request, currentUserId);

        return ResponseEntity.ok(boardMemberDto);
    }

    @DeleteMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID boardId,
            @PathVariable(value = "userId") UUID targetUserId
    ) {
        var currentUserId = getCurrentUserId();

        boardMembershipService.removeMember(boardId, targetUserId, currentUserId);

        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
