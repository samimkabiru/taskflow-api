package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.board.BoardDto;
import com.theninjadev.taskflowapi.dtos.board.CreateBoardRequest;
import com.theninjadev.taskflowapi.dtos.board.UpdateBoardRequest;
import com.theninjadev.taskflowapi.entities.Board;
import com.theninjadev.taskflowapi.entities.BoardMember;
import com.theninjadev.taskflowapi.enums.ActionType;
import com.theninjadev.taskflowapi.enums.BoardRole;
import com.theninjadev.taskflowapi.exceptions.BoardNotFoundException;
import com.theninjadev.taskflowapi.exceptions.InsufficientRoleException;
import com.theninjadev.taskflowapi.exceptions.NotBoardMemberException;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.BoardMapper;
import com.theninjadev.taskflowapi.repositories.BoardMemberRepository;
import com.theninjadev.taskflowapi.repositories.BoardRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final UserRepository userRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public BoardDto createBoard(CreateBoardRequest request, UUID currentUserId) {
        var owner = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        var board = new Board();
        board.setName(request.getName());
        board.setDescription(request.getDescription());
        board.setAccentColor(request.getAccentColor());
        board.setOwner(owner);
        board.setTaskPrefix(request.getTaskPrefix());

        boardRepository.saveAndFlush(board);

        var ownerMembership = new BoardMember();
        ownerMembership.setBoard(board);
        ownerMembership.setUser(owner);
        ownerMembership.setRole(BoardRole.OWNER);

        boardMemberRepository.save(ownerMembership);

        activityLogService.log(ActionType.BOARD_CREATED, board, null, owner, Map.of());
        return boardMapper.toDto(board);
    }

    public BoardDto getBoard(UUID boardId, UUID currentUserId) {
        var board = getBoardOrThrow(boardId);
        requireMembership(boardId, currentUserId);

        return boardMapper.toDto(board);
    }

    public List<BoardDto> listBoardsForUser(UUID currentUserId) {
        var boards = boardRepository.findAllForUser(currentUserId);

        return boards.stream().map(boardMapper::toDto).toList();
    }

    public BoardDto updateBoard(UUID boardId,@Valid UpdateBoardRequest request, UUID currentUserId) {
        var board = getBoardOrThrow(boardId);
        var member = boardMemberRepository.findByBoardIdAndUserId(boardId, currentUserId)
                .orElseThrow(NotBoardMemberException::new);

        if (!Set.of(BoardRole.OWNER, BoardRole.ADMIN).contains(member.getRole()))
            throw new InsufficientRoleException();

        if (request.getName() != null) board.setName(request.getName());
        if (request.getDescription() != null) board.setDescription(request.getDescription());
        if (request.getAccentColor() != null) board.setAccentColor(request.getAccentColor());
        if (request.getTaskPrefix() != null) board.setTaskPrefix(request.getTaskPrefix());

        boardRepository.save(board);

        return boardMapper.toDto(board);
    }

    public void deleteBoard(UUID boardId, UUID currentUserId) {
        var board = getBoardOrThrow(boardId);
        var member = boardMemberRepository.findByBoardIdAndUserId(boardId, currentUserId)
                .orElseThrow(NotBoardMemberException::new);

        if (member.getRole() != BoardRole.OWNER)
            throw new InsufficientRoleException();

        boardRepository.delete(board);
    }

    Board getBoardOrThrow(UUID boardId) {
        return boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
    }

    void requireMembership(UUID boardId, UUID currentUserId) {
        var isMember = boardMemberRepository.existsByBoardIdAndUserId(boardId, currentUserId);
        if (!isMember)
            throw new NotBoardMemberException();
    }

    BoardMember requireOwnerOrAdmin(UUID boardId, UUID currentUserId) {
        var currentBoardMember = boardMemberRepository
                .findByBoardIdAndUserId(boardId, currentUserId)
                .orElseThrow(NotBoardMemberException::new);

        if (!Set.of(BoardRole.OWNER, BoardRole.ADMIN).contains(currentBoardMember.getRole()))
            throw new InsufficientRoleException();

        return currentBoardMember;
    }

    BoardMember requireContributor(UUID boardId, UUID currentUserId) {
        var currentBoardMember = boardMemberRepository.findByBoardIdAndUserId(boardId, currentUserId)
                .orElseThrow(NotBoardMemberException::new);

        if (!Set.of(BoardRole.OWNER, BoardRole.MEMBER, BoardRole.ADMIN).contains(currentBoardMember.getRole()))
            throw new InsufficientRoleException();

        return currentBoardMember;
    }
}
