package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.tasklist.CreateTaskListRequest;
import com.theninjadev.taskflowapi.dtos.tasklist.ReorderTaskListRequest;
import com.theninjadev.taskflowapi.dtos.tasklist.TaskListDto;
import com.theninjadev.taskflowapi.dtos.tasklist.UpdateTaskListRequest;
import com.theninjadev.taskflowapi.dtos.websocket.BoardEvent;
import com.theninjadev.taskflowapi.entities.TaskList;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.ActionType;
import com.theninjadev.taskflowapi.exceptions.TaskListNotFoundException;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.TaskListMapper;
import com.theninjadev.taskflowapi.repositories.TaskListRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskListService {
    private final BoardService boardService;
    private final TaskListRepository taskListRepository;
    private final TaskListMapper taskListMapper;
    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TaskListDto createTaskList(UUID boardId, CreateTaskListRequest request, UUID currentUserId) {
        var board = boardService.getBoardOrThrow(boardId);

        boardService.requireContributor(boardId, currentUserId);

        double newPosition = taskListRepository
                .findTopByBoardIdOrderByPositionDesc(boardId)
                .map(list -> list.getPosition() + 1000.0)
                .orElse(1000.0);

        var taskList = new TaskList();

        taskList.setTitle(request.getTitle());
        taskList.setBoard(board);
        taskList.setPosition(newPosition);

        taskListRepository.saveAndFlush(taskList);
        var taskListDto = taskListMapper.toDto(taskList);

        activityLogService.log(ActionType.LIST_CREATED, board, null, getCurrentUser(currentUserId), Map.of("list_title", taskList.getTitle()));

        var event = new BoardEvent<>("LIST_CREATED", taskListDto);
        messagingTemplate.convertAndSend("/topic/boards/" + boardId, event);

        return taskListDto;
    }

    public List<TaskListDto> getTaskListsForBoard(UUID boardId, UUID currentUserId) {
        boardService.getBoardOrThrow(boardId);
        boardService.requireMembership(boardId, currentUserId);

        return taskListRepository
                .findByBoardIdOrderByPositionAsc(boardId)
                .stream()
                .map(taskListMapper::toDto)
                .toList();
    }

    @Transactional
    public TaskListDto updateTaskList(UUID listId, UpdateTaskListRequest request, UUID currentUserId) {
        var taskList = getTaskListAndVerifyContributor(listId, currentUserId);
        var oldTitle = taskList.getTitle();

        if (request.getTitle() != null) taskList.setTitle(request.getTitle());

        taskListRepository.save(taskList);
        var taskListDto = taskListMapper.toDto(taskList);

        if (request.getTitle() != null)
            activityLogService.log(ActionType.LIST_RENAMED, taskList.getBoard(), null, getCurrentUser(currentUserId), Map.of("old_title", oldTitle, "new_title", taskList.getTitle()));

        var event = new BoardEvent<>("LIST_RENAMED", taskListDto);
        messagingTemplate.convertAndSend("/topic/boards/" + taskList.getBoard().getId(), event);

        return taskListDto;
    }

    @Transactional
    public void deleteTaskList(UUID listId, UUID currentUserId) {
        var taskList = getTaskListAndVerifyContributor(listId, currentUserId);
        var taskListDto = taskListMapper.toDto(taskList);

        activityLogService.log(ActionType.LIST_DELETED, taskList.getBoard(), null, getCurrentUser(currentUserId), Map.of("list_title", taskList.getTitle()));

        var event = new BoardEvent<>("LIST_DELETED", taskListDto);
        messagingTemplate.convertAndSend("/topic/boards/" + taskList.getBoard().getId(), event);

        taskListRepository.delete(taskList);
    }

    public TaskListDto reorderTaskList(UUID listId, ReorderTaskListRequest request, UUID currentUserId) {
        var taskList = getTaskListAndVerifyContributor(listId, currentUserId);

        taskList.setPosition(request.getPosition());
        taskListRepository.save(taskList);
        var taskListDto = taskListMapper.toDto(taskList);

        var event = new BoardEvent<>("LIST_REORDERED", taskListDto);
        messagingTemplate.convertAndSend("/topic/boards/" + taskList.getBoard().getId(), event);

        return taskListDto;
    }

    private User getCurrentUser(UUID currentUserId) {
        return userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
    }

    TaskList getTaskListAndVerifyContributor(UUID listId, UUID currentUserId) {
        var taskList = taskListRepository.findById(listId).orElseThrow(TaskListNotFoundException::new);
        boardService.requireContributor(taskList.getBoard().getId(), currentUserId);
        return taskList;
    }
}
