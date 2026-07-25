package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    // Powers the board-level activity feed — paginated since this table only ever grows
    Page<ActivityLog> findByBoardIdOrderByCreatedAtDesc(UUID boardId, Pageable pageable);

    // For showing "history of this specific task" inside task detail
    List<ActivityLog> findByTaskIdOrderByCreatedAtDesc(UUID taskId);
}
