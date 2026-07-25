package com.theninjadev.taskflowapi.repositories;

import com.theninjadev.taskflowapi.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // All sessions (rows) for a user — e.g. for a "manage devices" screen
    List<RefreshToken> findByUserId(UUID userId);

    // Only this user's currently-valid sessions
    List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);

    // "Log out everywhere" — revoke every session for a user in one go
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId")
    void revokeAllByUserId(@Param("userId") UUID userId);
}
