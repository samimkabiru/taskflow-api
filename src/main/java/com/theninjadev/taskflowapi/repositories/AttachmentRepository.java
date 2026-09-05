package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    // Newest first — matches the task detail mockup's ordering
    List<Attachment> findByTaskIdOrderByCreatedAtDesc(UUID taskId);

    long countByTaskId(UUID taskId);
}
