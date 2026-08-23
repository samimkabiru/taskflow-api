package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.activitylog.ActivityLogDto;
import com.theninjadev.taskflowapi.entities.ActivityLog;
import com.theninjadev.taskflowapi.entities.Board;
import com.theninjadev.taskflowapi.entities.Task;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.ActionType;
import com.theninjadev.taskflowapi.exceptions.BoardNotFoundException;
import com.theninjadev.taskflowapi.exceptions.NotBoardMemberException;
import com.theninjadev.taskflowapi.exceptions.TaskNotFoundException;
import com.theninjadev.taskflowapi.mappers.ActivityLogMapper;
import com.theninjadev.taskflowapi.repositories.ActivityLogRepository;
import com.theninjadev.taskflowapi.repositories.BoardMemberRepository;
import com.theninjadev.taskflowapi.repositories.BoardRepository;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final TaskRepository taskRepository;

    public void log(ActionType actionType, Board board, Task task, User actor, Map<String, Object> metadata) {
        var activityLog = new ActivityLog();
        activityLog.setActionType(actionType);
        activityLog.setBoard(board);
        activityLog.setTask(task);
        activityLog.setActor(actor);
        activityLog.setMetadata(metadata != null ? metadata : Map.of());

        activityLogRepository.save(activityLog);
    }

    public Page<ActivityLogDto> listForBoard(UUID boardId, UUID currentUserId, Pageable pageable) {
        if (!boardRepository.existsById(boardId))
            throw new BoardNotFoundException();

        if (!boardMemberRepository.existsByBoardIdAndUserId(boardId, currentUserId))
            throw new NotBoardMemberException();

        return activityLogRepository
                .findByBoardIdOrderByCreatedAtDesc(boardId, pageable)
                .map(activityLogMapper::toDto);
    }

    public List<ActivityLogDto> listForTask(UUID taskId, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var boardId = task.getBoard().getId();

        if (!boardRepository.existsById(boardId))
            throw new BoardNotFoundException();

        if (!boardMemberRepository.existsByBoardIdAndUserId(boardId, currentUserId))
            throw new NotBoardMemberException();

        return activityLogRepository
                .findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(activityLogMapper::toDto)
                .toList();
    }
}
