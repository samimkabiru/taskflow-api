package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.tasklist.CreateTaskListRequest;
import com.theninjadev.taskflowapi.dtos.tasklist.ReorderTaskListRequest;
import com.theninjadev.taskflowapi.dtos.tasklist.TaskListDto;
import com.theninjadev.taskflowapi.dtos.tasklist.UpdateTaskListRequest;
import com.theninjadev.taskflowapi.entities.TaskList;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.ActionType;
import com.theninjadev.taskflowapi.exceptions.TaskListNotFoundException;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.TaskListMapper;
import com.theninjadev.taskflowapi.repositories.TaskListRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
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

        activityLogService.log(ActionType.LIST_CREATED, board, null, getCurrentUser(currentUserId), Map.of("list_title", taskList.getTitle()));
        return taskListMapper.toDto(taskList);
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
        var tasklist = getTaskListAndVerifyContributor(listId, currentUserId);
        var oldTitle = tasklist.getTitle();

        if (request.getTitle() != null) tasklist.setTitle(request.getTitle());

        taskListRepository.save(tasklist);

        if (request.getTitle() != null)
            activityLogService.log(ActionType.LIST_RENAMED, tasklist.getBoard(), null, getCurrentUser(currentUserId), Map.of("old_title", oldTitle, "new_title", tasklist.getTitle()));
        return taskListMapper.toDto(tasklist);
    }

    @Transactional
    public void deleteTaskList(UUID listId, UUID currentUserId) {
        var tasklist = getTaskListAndVerifyContributor(listId, currentUserId);

        activityLogService.log(ActionType.LIST_DELETED, tasklist.getBoard(), null, getCurrentUser(currentUserId), Map.of("list_title", tasklist.getTitle()));
        taskListRepository.delete(tasklist);
    }

    public TaskListDto reorderTaskList(UUID listId, ReorderTaskListRequest request, UUID currentUserId) {
        var tasklist = getTaskListAndVerifyContributor(listId, currentUserId);

        tasklist.setPosition(request.getPosition());
        taskListRepository.save(tasklist);

        return taskListMapper.toDto(tasklist);
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
