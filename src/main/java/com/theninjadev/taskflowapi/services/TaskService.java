package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.task.CreateTaskRequest;
import com.theninjadev.taskflowapi.dtos.task.MoveTaskRequest;
import com.theninjadev.taskflowapi.dtos.task.TaskDto;
import com.theninjadev.taskflowapi.dtos.task.UpdateTaskRequest;
import com.theninjadev.taskflowapi.dtos.websocket.BoardEvent;
import com.theninjadev.taskflowapi.entities.Task;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.ActionType;
import com.theninjadev.taskflowapi.enums.NotificationType;
import com.theninjadev.taskflowapi.enums.TaskPriority;
import com.theninjadev.taskflowapi.exceptions.*;
import com.theninjadev.taskflowapi.mappers.TaskMapper;
import com.theninjadev.taskflowapi.repositories.BoardMemberRepository;
import com.theninjadev.taskflowapi.repositories.TaskListRepository;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    private final TaskListRepository taskListRepository;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TaskDto createTask(UUID taskListId, CreateTaskRequest request, UUID currentUserId) {
        var taskList = taskListService.getTaskListAndVerifyContributor(taskListId, currentUserId);
        var board = taskList.getBoard();
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        var newCounter = taskRepository.incrementTaskCounter(board.getId());
        var priority = parsePriority(request.getPriority());
        var assignee = resolveAssignee(request.getAssigneeId(), board.getId());

        double newPosition = taskRepository
                .findTopByTaskListIdOrderByPositionDesc(taskList.getId())
                .map(task -> task.getPosition() + 10_000.0)
                .orElse(10_000.0);

        var task = new Task();
        task.setTitle(request.getTitle());
        task.setPosition(newPosition);
        task.setTaskList(taskList);
        task.setShortCode(board.getTaskPrefix() + "-" + newCounter);
        task.setBoard(board);
        task.setCreatedBy(currentUser);

        if (request.getAssigneeId() != null) task.setAssignee(assignee);
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getPriority() != null) task.setPriority(priority);

        taskRepository.saveAndFlush(task);
        activityLogService.log(ActionType.TASK_CREATED, board, task, currentUser, Map.of("short_code", task.getShortCode()));

        if (request.getAssigneeId() != null) {
            notificationService.notify(NotificationType.ASSIGNMENT, assignee, Map.of("assigner_name", currentUser.getFullName(), "task_title", task.getTitle(), "short_code", task.getShortCode()));
        }

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

    public List<TaskDto> getTasksForList(UUID taskListId, UUID currentUserId) {
        var taskList = taskListRepository.findById(taskListId).orElseThrow(TaskListNotFoundException::new);
        boardService.requireMembership(taskList.getBoard().getId(), currentUserId);

        return taskRepository
                .findByTaskListIdOrderByPositionAsc(taskListId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional
    public TaskDto updateTask(UUID taskId, UpdateTaskRequest request, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        var oldPriority = task.getPriority();
        boardService.requireContributor(task.getBoard().getId(), currentUserId);

        var priority = parsePriority(request.getPriority());
        var assignee = resolveAssignee(request.getAssigneeId(), task.getBoard().getId());

        if (request.getAssigneeId() != null) task.setAssignee(assignee);
        if (request.getPriority() != null) task.setPriority(priority);
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        taskRepository.save(task);

        if (request.getTitle() != null || request.getDescription() != null || request.getDueDate() != null)
            activityLogService.log(ActionType.TASK_UPDATED, task.getBoard(), task, currentUser, Map.of("short_code", task.getShortCode()));

        if (request.getPriority() != null)
            activityLogService.log(ActionType.PRIORITY_CHANGED, task.getBoard(), task, currentUser, Map.of("short_code", task.getShortCode(), "from", oldPriority != null ? oldPriority.name() : "none", "to", priority.name()));

        if (request.getAssigneeId() != null) {
            activityLogService.log(ActionType.ASSIGNEE_CHANGED, task.getBoard(), task, currentUser, Map.of("short_code", task.getShortCode(), "assignee_name", assignee.getFullName()));
            notificationService.notify(NotificationType.ASSIGNMENT, assignee, Map.of("assigner_name", currentUser.getFullName(), "task_title", task.getTitle(), "short_code", task.getShortCode()));
        }

        return taskMapper.toDto(task);
    }


    @Transactional
    public void deleteTask(UUID taskId, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        boardService.requireContributor(task.getBoard().getId(), currentUserId);

        activityLogService.log(ActionType.TASK_DELETED, task.getBoard(), task, currentUser, Map.of("short_code", task.getShortCode(), "title", task.getTitle()));
        taskRepository.delete(task);
    }

    @Transactional
    public TaskDto moveTask(UUID taskId, MoveTaskRequest request, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var boardId = task.getBoard().getId();
        var oldList = task.getTaskList();
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        boardService.requireContributor(task.getBoard().getId(), currentUserId);

        var newList = taskListRepository.findById(request.getTaskListId())
                .orElseThrow(TaskListNotFoundException::new);

        if (!newList.getBoard().getId().equals(task.getBoard().getId()))
            throw new TaskListNotOnBoardException();

        if (task.getTaskList().getId().equals(request.getTaskListId())
                && task.getPosition().equals(request.getPosition())) {
            return taskMapper.toDto(task);
        }

        task.setTaskList(newList);
        task.setPosition(request.getPosition());

        taskRepository.save(task);
        var taskDto = taskMapper.toDto(task);

        activityLogService.log(ActionType.TASK_MOVED, task.getBoard(), task, currentUser, Map.of("short_code", task.getShortCode(), "from_list", oldList.getTitle(), "to_list", newList.getTitle()));

        if (task.getAssignee() != null && !task.getAssignee().getId().equals(currentUserId))
            notificationService.notify(NotificationType.STATUS_CHANGE, task.getAssignee(), Map.of("mover_name", currentUser.getFullName(), "task_title", task.getTitle(), "to_list", newList.getTitle()));

        var event = new BoardEvent<>("TASK_MOVED", taskDto);
        messagingTemplate.convertAndSend("/topic/boards/" + boardId, event);

        return taskDto;
    }

    private TaskPriority parsePriority(String priorityStr) {
        if (priorityStr == null) return null;
        try {
            return TaskPriority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTaskPriorityException();
        }
    }

    private User resolveAssignee(UUID assigneeId, UUID boardId) {
        if (assigneeId == null) return null;
        var isMember = boardMemberRepository.existsByBoardIdAndUserId(boardId, assigneeId);
        if (!isMember)
            throw new AssigneeNotBoardMemberException();

        return userRepository.findById(assigneeId).orElseThrow(UserNotFoundException::new);
    }
}
