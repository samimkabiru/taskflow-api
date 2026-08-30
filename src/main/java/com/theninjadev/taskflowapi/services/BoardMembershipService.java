package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.board.BoardInviteDto;
import com.theninjadev.taskflowapi.dtos.board.BoardMemberDto;
import com.theninjadev.taskflowapi.dtos.board.InviteMemberRequest;
import com.theninjadev.taskflowapi.dtos.board.UpdateMemberRoleRequest;
import com.theninjadev.taskflowapi.dtos.websocket.BoardEvent;
import com.theninjadev.taskflowapi.entities.BoardInvite;
import com.theninjadev.taskflowapi.entities.BoardMember;
import com.theninjadev.taskflowapi.enums.*;
import com.theninjadev.taskflowapi.exceptions.*;
import com.theninjadev.taskflowapi.mappers.BoardMapper;
import com.theninjadev.taskflowapi.repositories.BoardInviteRepository;
import com.theninjadev.taskflowapi.repositories.BoardMemberRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BoardMembershipService {
    private final BoardMemberRepository boardMemberRepository;
    private final BoardMapper boardMapper;
    private final BoardInviteRepository boardInviteRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;


    public List<BoardMemberDto> getBoardMembers(UUID boardId, UUID currentUserId) {
        boardService.getBoardOrThrow(boardId);
        var isMember = boardMemberRepository.existsByBoardIdAndUserId(boardId, currentUserId);

        if (!isMember)
            throw new NotBoardMemberException();

        var boardMembers = boardMemberRepository.findByBoardId(boardId);

        return boardMembers.stream().map(boardMapper::toDto).toList();
    }

    @Transactional
    public BoardInviteDto inviteMember(UUID boardId, InviteMemberRequest request, UUID currentUserId) {
        var email = request.getEmail().trim().toLowerCase();
        var board = boardService.getBoardOrThrow(boardId);
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        var currentBoardMember = boardService.requireOwnerOrAdmin(boardId, currentUserId);

        var invitedUser = userRepository.findByEmail(email).orElse(null);
        var invitedUserIsMember = invitedUser != null && boardMemberRepository.existsByBoardIdAndUserId(boardId, invitedUser.getId());

        if (invitedUserIsMember)
            throw new AlreadyBoardMemberException();

        BoardInviteRole role;
        try {
            role = BoardInviteRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidBoardRoleException();
        }

        var boardInvite = new BoardInvite();
        boardInvite.setBoard(board);
        boardInvite.setEmail(email);
        boardInvite.setRole(role);
        boardInvite.setStatus(InviteStatus.PENDING);
        boardInvite.setInvitedBy(currentBoardMember.getUser());

        try {
            boardInviteRepository.save(boardInvite);
        } catch (DataIntegrityViolationException e) {
            throw new InviteAlreadyPendingException();
        }
        var boardInviteDto = boardMapper.toDto(boardInvite);

        if (invitedUser != null) {
            activityLogService.log(ActionType.MEMBER_INVITED, board, null, currentUser, Map.of("invited_name", invitedUser.getFullName(), "role", role.name()));
            notificationService.notify(NotificationType.INVITE, invitedUser, Map.of("inviter_name", currentUser.getFullName(), "board_name", board.getName(), "role", role.name(), "invite_id", boardInvite.getId()));
        }

        var event = new BoardEvent<>("MEMBER_INVITED", boardInviteDto);
        messagingTemplate.convertAndSend("/topic/boards/" + boardId, event);

        return boardInviteDto;
    }

    @Transactional
    public BoardMemberDto acceptInvite(UUID inviteId, UUID currentUserId) {
        var invite = boardInviteRepository.findById(inviteId).orElseThrow(InviteNotFoundException::new);
        var board = invite.getBoard();
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        if (invite.getStatus() != InviteStatus.PENDING)
            throw new InviteNotPendingException();

        if (!currentUser.getEmail().equalsIgnoreCase(invite.getEmail()))
            throw new InviteEmailMismatchException();

        invite.setStatus(InviteStatus.ACCEPTED);

        boardInviteRepository.save(invite);

        var boardMember = new BoardMember();
        boardMember.setUser(currentUser);
        boardMember.setBoard(board);
        boardMember.setRole(BoardRole.valueOf(invite.getRole().name()));

        try {
            boardMemberRepository.saveAndFlush(boardMember);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyBoardMemberException();
        }

        var boardMemberDto = boardMapper.toDto(boardMember);

        activityLogService.log(ActionType.MEMBER_ADDED, invite.getBoard(), null, currentUser, Map.of("role", invite.getRole().name()));
        notificationService.notify(NotificationType.INVITE, invite.getInvitedBy(), Map.of("accepter_name", currentUser.getFullName(), "board_name", board.getName(), "role", invite.getRole().name()));

        var event = new BoardEvent<>("MEMBER_ADDED", boardMemberDto);
        messagingTemplate.convertAndSend("/topic/boards/" + board.getId(), event);

        return boardMemberDto;
    }

    @Transactional
    public void declineInvite(UUID inviteId, UUID currentUserId) {
        var invite = boardInviteRepository.findById(inviteId).orElseThrow(InviteNotFoundException::new);
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        if (invite.getStatus() != InviteStatus.PENDING)
            throw new InviteNotPendingException();

        if (!currentUser.getEmail().equalsIgnoreCase(invite.getEmail()))
            throw new InviteEmailMismatchException();

        markInviteRevoked(invite);
    }

    @Transactional
    public void revokeInvite(UUID inviteId, UUID currentUserId) {
        var invite = boardInviteRepository.findById(inviteId).orElseThrow(InviteNotFoundException::new);

        if (invite.getStatus() != InviteStatus.PENDING)
            throw new InviteNotPendingException();

        boardService.requireOwnerOrAdmin(invite.getBoard().getId(), currentUserId);

        markInviteRevoked(invite);
    }

    @Transactional
    public BoardMemberDto updateMemberRole(
            UUID boardId,
            UUID targetUserId,
            UpdateMemberRoleRequest request,
            UUID currentUserId) {
        var board = boardService.getBoardOrThrow(boardId);
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        boardService.requireOwnerOrAdmin(boardId, currentUserId);

        var targetBoardMember = guardTargetNotOwner(boardId, targetUserId);

        BoardRole role;
        try {
            role = BoardRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidBoardRoleException();
        }

        if (role == BoardRole.OWNER)
            throw new InvalidBoardRoleException();

        targetBoardMember.setRole(role);

        var boardMemberDto = boardMapper.toDto(targetBoardMember);

        activityLogService.log(ActionType.MEMBER_ROLE_CHANGED, board, null, currentUser, Map.of("member_name", targetBoardMember.getUser().getFullName(), "new_role", role.name()));
        boardMemberRepository.save(targetBoardMember);


        var event = new BoardEvent<>("MEMBER_ROLE_CHANGED", boardMemberDto);
        messagingTemplate.convertAndSend("/topic/boards/" + boardId, event);

        return boardMemberDto;
    }

    @Transactional
    public void removeMember(UUID boardId, UUID targetUserId, UUID currentUserId) {
        var board = boardService.getBoardOrThrow(boardId);
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        boardService.requireOwnerOrAdmin(boardId, currentUserId);

        var targetBoardMember = guardTargetNotOwner(boardId, targetUserId);
        var boardMemberDto = boardMapper.toDto(targetBoardMember);

        activityLogService.log(ActionType.MEMBER_REMOVED, board, null, currentUser, Map.of("removed_name", targetBoardMember.getUser().getFullName()));

        var event = new BoardEvent<>("MEMBER_REMOVED", boardMemberDto);
        messagingTemplate.convertAndSend("/topic/boards/" + board.getId(), event);

        boardMemberRepository.delete(targetBoardMember);

    }

    public void leaveBoard(UUID boardId, UUID currentUserId) {
        boardService.getBoardOrThrow(boardId);

        var targetBoardMember = guardTargetNotOwner(boardId, currentUserId);

        var event = new BoardEvent<>("MEMBER_REMOVED", boardMapper.toDto(targetBoardMember));
        messagingTemplate.convertAndSend("/topic/boards/" + boardId, event);

        boardMemberRepository.delete(targetBoardMember);
    }

    private void markInviteRevoked(BoardInvite invite) {
        invite.setStatus(InviteStatus.REVOKED);
        boardInviteRepository.save(invite);
    }

    private BoardMember guardTargetNotOwner(UUID boardId, UUID targetUserId) {
        var targetBoardMember = boardMemberRepository
                .findByBoardIdAndUserId(boardId, targetUserId)
                .orElseThrow(NotBoardMemberException::new);

        if (targetBoardMember.getRole() == BoardRole.OWNER)
            throw new CannotRemoveOwnerException();

        return targetBoardMember;
    }
}
