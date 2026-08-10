package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.task.CreateTaskRequest;
import com.theninjadev.taskflowapi.dtos.task.TaskDto;
import com.theninjadev.taskflowapi.entities.Task;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.TaskPriority;
import com.theninjadev.taskflowapi.exceptions.AssigneeNotBoardMemberException;
import com.theninjadev.taskflowapi.exceptions.InvalidTaskPriorityException;
import com.theninjadev.taskflowapi.exceptions.TaskNotFoundException;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.TaskMapper;
import com.theninjadev.taskflowapi.repositories.BoardMemberRepository;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskListService taskListService;
    private final TaskRepository taskRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final BoardService boardService;

    @Transactional
    public TaskDto createTask(UUID taskListId, CreateTaskRequest request, UUID currentUserId) {
        var taskList = taskListService.getTaskListAndVerifyContributor(taskListId, currentUserId);
        var board = taskList.getBoard();
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        var newCounter = taskRepository.incrementTaskCounter(board.getId());

        TaskPriority priority = null;
        if (request.getPriority() != null) {
            try {
                priority = TaskPriority.valueOf(request.getPriority().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTaskPriorityException();
            }
        }

        User assignee = null;
        if (request.getAssigneeId() != null) {
            var isMember = boardMemberRepository.existsByBoardIdAndUserId(board.getId(), request.getAssigneeId());
            if (!isMember)
                throw new AssigneeNotBoardMemberException();
            assignee = userRepository.findById(request.getAssigneeId()).orElseThrow(UserNotFoundException::new);
        }

        double newPosition = taskRepository
                .findTopByTaskListIdOrderByPositionDesc(taskList.getId())
                .map(task -> task.getPosition() + 10_000.0)
                .orElse(10_000.0);

        var task = new Task();
        task.setTitle(request.getTitle());
        task.setPosition(newPosition);
        task.setTaskList(taskList);
        task.setAssignee(assignee);
        task.setShortCode(board.getTaskPrefix() + "-" + newCounter);
        task.setBoard(board);
        task.setCreatedBy(currentUser);

        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getPriority() != null) task.setPriority(priority);

        taskRepository.saveAndFlush(task);
        return taskMapper.toDto(task);
    }

    public TaskDto getTask(UUID taskId, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        boardService.requireMembership(task.getBoard().getId(), currentUserId);

        return taskMapper.toDto(task);
    }

    public Page<TaskDto> getTasksForBoard(UUID boardId, Pageable pageable, UUID currentUserId) {
        boardService.getBoardOrThrow(boardId);
        boardService.requireMembership(boardId, currentUserId);

        return taskRepository.findByBoardId(boardId, pageable).map(taskMapper::toDto);
    }
}
