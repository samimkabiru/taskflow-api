package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LabelRepository extends JpaRepository<Label, UUID> {

    List<Label> findByBoardId(UUID boardId);

    boolean existsByBoardIdAndName(UUID boardId, String name);
}
