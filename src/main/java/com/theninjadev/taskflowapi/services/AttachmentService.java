package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.attachment.AttachmentDto;
import com.theninjadev.taskflowapi.entities.Attachment;
import com.theninjadev.taskflowapi.exceptions.EmptyFileException;
import com.theninjadev.taskflowapi.exceptions.FileTooLargeException;
import com.theninjadev.taskflowapi.exceptions.TaskNotFoundException;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.AttachmentMapper;
import com.theninjadev.taskflowapi.repositories.AttachmentRepository;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final TaskRepository taskRepository;
    private final BoardService boardService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final AttachmentMapper attachmentMapper;
    private final AttachmentRepository attachmentRepository;

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;

    @Transactional
    public AttachmentDto uploadAttachment(UUID taskId, MultipartFile file, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var boardId = task.getBoard().getId();
        var currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        boardService.requireContributor(boardId, currentUserId);

        if (file.isEmpty())
            throw new EmptyFileException();

        if (file.getSize() > maxFileSize.toBytes())
            throw new FileTooLargeException();

        var storageKey = fileStorageService.store(file);

        var attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setFileSizeBytes(file.getSize());
        attachment.setStorageKey(storageKey);
        attachment.setUploadedBy(currentUser);
        attachment.setTask(task);

        attachmentRepository.saveAndFlush(attachment);
        return attachmentMapper.toDto(attachment);

    }

    public List<AttachmentDto> getAttachmentsForTask(UUID taskId, UUID currentUserId) {
        var task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        var boardId = task.getBoard().getId();
        boardService.requireMembership(boardId, currentUserId);

        return attachmentRepository
                .findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(attachmentMapper::toDto)
                .toList();
    }
}
