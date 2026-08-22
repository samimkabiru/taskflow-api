package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.comment.CommentDto;
import com.theninjadev.taskflowapi.dtos.comment.CreateCommentRequest;
import com.theninjadev.taskflowapi.dtos.comment.UpdateCommentRequest;
import com.theninjadev.taskflowapi.entities.Comment;
import com.theninjadev.taskflowapi.enums.ActionType;
import com.theninjadev.taskflowapi.exceptions.CommentNotFoundException;
import com.theninjadev.taskflowapi.exceptions.NotCommentAuthorException;
import com.theninjadev.taskflowapi.exceptions.TaskNotFoundException;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.CommentMapper;
import com.theninjadev.taskflowapi.repositories.CommentRepository;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentService {
    private final TaskRepository taskRepository;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ActivityLogService activityLogService;

    public CommentDto createComment(UUID taskId, CreateCommentRequest request, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var boardId = task.getBoard().getId();
        var author = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
        boardService.requireContributor(boardId, currentUserId);

        var comment = new Comment();
        comment.setTask(task);
        comment.setContent(request.getContent());
        comment.setAuthor(author);

        commentRepository.save(comment);
        activityLogService.log(ActionType.COMMENT_ADDED, task.getBoard(), task, author, Map.of("task_title", task.getTitle()));
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getCommentsForTask(UUID taskId, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var boardId = task.getBoard().getId();
        boardService.requireMembership(boardId, currentUserId);

        return commentRepository
                .findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public CommentDto updateComment(UUID commentId, UpdateCommentRequest request, UUID currentUserId) {
        var comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        if (!comment.getAuthor().getId().equals(currentUserId))
            throw new NotCommentAuthorException();

        comment.setContent(request.getContent());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public void deleteComment(UUID commentId, UUID currentUserId) {
        var comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        var boardId = comment.getTask().getBoard().getId();

        if (!comment.getAuthor().getId().equals(currentUserId)) {
            boardService.requireOwnerOrAdmin(boardId, currentUserId);
        }

        commentRepository.delete(comment);
    }
}
