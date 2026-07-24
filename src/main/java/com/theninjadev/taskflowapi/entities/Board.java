package com.theninjadev.taskflowapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "accent_color")
    private String accentColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "task_counter")
    private Integer taskCounter;

    @Column(name = "task_prefix")
    private String taskPrefix;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "board")
    private Set<BoardInvite> boardInvites = new LinkedHashSet<>();

    @OneToMany(mappedBy = "board")
    private Set<BoardMember> boardMembers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "board")
    private List<TaskList> taskLists = new ArrayList<>();
}