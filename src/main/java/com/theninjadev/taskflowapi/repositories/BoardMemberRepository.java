package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {

    // The core lookup for every permission check: "what role does this user have on this board?"
    Optional<BoardMember> findByBoardIdAndUserId(UUID boardId, UUID userId);

    boolean existsByBoardIdAndUserId(UUID boardId, UUID userId);

    List<BoardMember> findByBoardId(UUID boardId);

    List<BoardMember> findByUserId(UUID userId);
}
