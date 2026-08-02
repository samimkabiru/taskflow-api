package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.board.BoardDto;
import com.theninjadev.taskflowapi.dtos.board.CreateBoardRequest;
import com.theninjadev.taskflowapi.entities.Board;
import com.theninjadev.taskflowapi.entities.BoardMember;
import com.theninjadev.taskflowapi.enums.BoardRole;
import com.theninjadev.taskflowapi.exceptions.BoardNotFoundException;
import com.theninjadev.taskflowapi.exceptions.NotBoardMemberException;
import com.theninjadev.taskflowapi.mappers.BoardMapper;
import com.theninjadev.taskflowapi.repositories.BoardMemberRepository;
import com.theninjadev.taskflowapi.repositories.BoardRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final UserRepository userRepository;
    private final BoardMemberRepository boardMemberRepository;

    @Transactional
    public BoardDto createBoard(CreateBoardRequest request, UUID currentUserId) {
        var owner = userRepository.findById(currentUserId).orElseThrow();
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

        return boardMapper.toDto(board);
    }

    public BoardDto getBoard(UUID boardId, UUID currentUserId) {
        var board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        var isMember = boardMemberRepository.existsByBoardIdAndUserId(boardId, currentUserId);
        if (!isMember)
            throw new NotBoardMemberException();

        return boardMapper.toDto(board);
    }
}
