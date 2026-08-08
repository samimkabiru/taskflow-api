package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskListRepository extends JpaRepository<TaskList, UUID> {

    // Powers the kanban board view — columns in their correct left-to-right order
    List<TaskList> findByBoardIdOrderByPositionAsc(UUID boardId);

    Optional<TaskList> findTopByBoardIdOrderByPositionDesc(UUID boardId);
}
