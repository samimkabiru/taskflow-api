package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.comment.CommentDto;
import com.theninjadev.taskflowapi.dtos.comment.CreateCommentRequest;
import com.theninjadev.taskflowapi.entities.Comment;
import com.theninjadev.taskflowapi.exceptions.TaskNotFoundException;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.CommentMapper;
import com.theninjadev.taskflowapi.repositories.CommentRepository;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentService {
    private final TaskRepository taskRepository;
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

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
        return commentMapper.toDto(comment);
    }
}
