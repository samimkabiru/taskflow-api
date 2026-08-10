package com.theninjadev.taskflowapi.entities;

import com.theninjadev.taskflowapi.enums.BoardInviteRole;
import com.theninjadev.taskflowapi.enums.InviteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "board_invites")
public class BoardInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    
    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private BoardInviteRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InviteStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}