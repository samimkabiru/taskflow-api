package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // Oldest first — matches a normal comment-thread reading order
    List<Comment> findByTaskIdOrderByCreatedAtAsc(UUID taskId);

    long countByTaskId(UUID taskId);
}
