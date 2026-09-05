package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, TaskRepositoryCustom {

    // Tasks within one list, in drag-order — the bread-and-butter query for rendering a column
    List<Task> findByTaskListIdOrderByPositionAsc(UUID taskListId);

    List<Task> findByAssigneeId(UUID assigneeId);

    // Paginated, board-scoped — avoids ever loading "all tasks on a board" unbounded
    Page<Task> findByBoardId(UUID boardId, Pageable pageable);

    List<Task> findByDueDateAndAssigneeIsNotNull(LocalDate dueDate);

    Page<Task> findByBoardIdAndAssigneeId(UUID boardId, UUID assigneeId, Pageable pageable);

    // Needed for the short_code uniqueness check / lookups like "open TSK-142"
    Optional<Task> findByBoardIdAndShortCode(UUID boardId, String shortCode);

    Optional<Task> findTopByTaskListIdOrderByPositionDesc(UUID taskListId);

    // Note: richer filtering (by label, due-date range, multiple criteria combined) is
    // a good candidate for a JPA Specification later rather than more derived-query methods —
    // worth revisiting once the board-level filter UI is being wired up.
}
