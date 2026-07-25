package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.BoardInvite;
import com.theninjadev.taskflowapi.enums.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardInviteRepository extends JpaRepository<BoardInvite, UUID> {

    List<BoardInvite> findByBoardIdAndStatus(UUID boardId, InviteStatus status);

    // Used when someone registers/accepts — check for a pending invite matching their email
    Optional<BoardInvite> findByBoardIdAndEmailAndStatus(UUID boardId, String email, InviteStatus status);

    List<BoardInvite> findByEmailAndStatus(String email, InviteStatus status);
}
