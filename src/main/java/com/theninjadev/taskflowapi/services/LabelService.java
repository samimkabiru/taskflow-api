package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.label.AssignLabelsRequest;
import com.theninjadev.taskflowapi.dtos.label.CreateLabelRequest;
import com.theninjadev.taskflowapi.dtos.label.LabelDto;
import com.theninjadev.taskflowapi.dtos.label.UpdateLabelRequest;
import com.theninjadev.taskflowapi.entities.Label;
import com.theninjadev.taskflowapi.exceptions.DuplicateLabelNameException;
import com.theninjadev.taskflowapi.exceptions.LabelNotFoundException;
import com.theninjadev.taskflowapi.exceptions.LabelNotOnBoardException;
import com.theninjadev.taskflowapi.exceptions.TaskNotFoundException;
import com.theninjadev.taskflowapi.mappers.LabelMapper;
import com.theninjadev.taskflowapi.repositories.LabelRepository;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LabelService {
    private final BoardService boardService;
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final TaskRepository taskRepository;

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
        var label = labelRepository.findById(labelId).orElseThrow(LabelNotFoundException::new);
        var boardId = label.getBoard().getId();
        boardService.requireContributor(boardId, currentUserId);

        if (request.getName() != null) {
            var name = request.getName().trim();
            if (!name.equalsIgnoreCase(label.getName())) {
                if (labelRepository.existsByBoardIdAndName(boardId, name))
                    throw new DuplicateLabelNameException();
            }
            label.setName(name);
        }

        if (request.getColor() != null) label.setColor(request.getColor().toLowerCase());

        labelRepository.save(label);
        return labelMapper.toDto(label);
    }

    public void deleteLabel(UUID labelId, UUID currentUserId) {
        var label = labelRepository.findById(labelId).orElseThrow(LabelNotFoundException::new);
        var boardId = label.getBoard().getId();
        boardService.requireContributor(boardId, currentUserId);

        labelRepository.delete(label);
    }

    public void assignLabelToTask(UUID taskId, AssignLabelsRequest request, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var boardId = task.getBoard().getId();
        boardService.requireContributor(boardId, currentUserId);

        var labels = labelRepository.findAllById(request.getLabelIds());

        if (labels.size() != request.getLabelIds().size())
            throw new LabelNotFoundException();

        labels.forEach(label -> {
            if (!label.getBoard().getId().equals(boardId))
                throw new LabelNotOnBoardException();
        });

        task.getLabels().clear();
        task.getLabels().addAll(labels);
        taskRepository.save(task);
    }
}
