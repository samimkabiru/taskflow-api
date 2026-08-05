package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.board.BoardMemberDto;
import com.theninjadev.taskflowapi.exceptions.NotBoardMemberException;
import com.theninjadev.taskflowapi.mappers.BoardMapper;
import com.theninjadev.taskflowapi.repositories.BoardMemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BoardMembershipService {
    private final BoardMemberRepository boardMemberRepository;
    private final BoardMapper boardMapper;

    public List<BoardMemberDto> getBoardMembers(UUID boardId, UUID currentUserId) {
        var isMember = boardMemberRepository.existsByBoardIdAndUserId(boardId, currentUserId);

        if (!isMember)
            throw new NotBoardMemberException();

        var boardMembers = boardMemberRepository.findByBoardId(boardId);

        return boardMembers.stream().map(boardMapper::toDto).toList();
    }
}
