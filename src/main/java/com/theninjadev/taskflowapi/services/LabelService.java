package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.label.CreateLabelRequest;
import com.theninjadev.taskflowapi.dtos.label.LabelDto;
import com.theninjadev.taskflowapi.entities.Label;
import com.theninjadev.taskflowapi.exceptions.BoardNotFoundException;
import com.theninjadev.taskflowapi.exceptions.DuplicateLabelNameException;
import com.theninjadev.taskflowapi.mappers.LabelMapper;
import com.theninjadev.taskflowapi.repositories.BoardRepository;
import com.theninjadev.taskflowapi.repositories.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LabelService {
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelDto createLabel(UUID boardId, CreateLabelRequest request, UUID currentUserId) {
        var board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        var name = request.getName().trim();
        boardService.requireContributor(boardId, currentUserId);

        var labelExistsInBoard = labelRepository.existsByBoardIdAndName(boardId, name);
        if (labelExistsInBoard)
            throw new DuplicateLabelNameException();

        var label = new Label();
        label.setName(name);
        label.setBoard(board);
        label.setColor(request.getColor().toLowerCase());

        try {
            labelRepository.save(label);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateLabelNameException();
        }
        return labelMapper.toDto(label);
    }
}
