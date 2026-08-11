package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.label.CreateLabelRequest;
import com.theninjadev.taskflowapi.dtos.label.LabelDto;
import com.theninjadev.taskflowapi.dtos.label.UpdateLabelRequest;
import com.theninjadev.taskflowapi.entities.Label;
import com.theninjadev.taskflowapi.exceptions.DuplicateLabelNameException;
import com.theninjadev.taskflowapi.exceptions.LabelNotFoundException;
import com.theninjadev.taskflowapi.mappers.LabelMapper;
import com.theninjadev.taskflowapi.repositories.BoardRepository;
import com.theninjadev.taskflowapi.repositories.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LabelService {
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelDto createLabel(UUID boardId, CreateLabelRequest request, UUID currentUserId) {
        var name = request.getName().trim();
        var board = boardService.getBoardOrThrow(boardId);
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

    public List<LabelDto> getLabelsForBoard(UUID boardId, UUID currentUserId) {
        boardService.getBoardOrThrow(boardId);
        boardService.requireMembership(boardId, currentUserId);

        return labelRepository
                .findByBoardId(boardId)
                .stream()
                .map(labelMapper::toDto)
                .toList();
    }

    public LabelDto updateLabel(UUID labelId, UpdateLabelRequest request, UUID currentUserId) {
        var name = request.getName().trim();
        var label = labelRepository.findById(labelId).orElseThrow(LabelNotFoundException::new);
        var boardId = label.getBoard().getId();
        boardService.requireContributor(boardId, currentUserId);

        if (request.getName() != null) {
            var labelExistsInBoard = labelRepository.existsByBoardIdAndName(boardId, name);
            if (labelExistsInBoard)
                throw new DuplicateLabelNameException();
        }

        if (request.getName() != null) label.setName(request.getName().trim());
        if (request.getColor() != null) label.setColor(request.getColor().toLowerCase());

        labelRepository.save(label);
        return labelMapper.toDto(label);
    }
}
