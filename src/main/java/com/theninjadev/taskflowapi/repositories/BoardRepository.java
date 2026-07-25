package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {

    List<Board> findByOwnerId(UUID ownerId);

    // Every board a user belongs to, regardless of role — powers the boards dashboard
    @Query("""
            SELECT bm.board FROM BoardMember bm
            WHERE bm.user.id = :userId
            """)
    List<Board> findAllForUser(@Param("userId") UUID userId);
}
