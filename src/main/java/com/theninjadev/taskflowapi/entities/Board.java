package com.theninjadev.taskflowapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private Integer taskCounter = 0;

    @Column(name = "task_prefix")
    private String taskPrefix = "TSK";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "board")
    private Set<BoardInvite> boardInvites = new LinkedHashSet<>();

    @OneToMany(mappedBy = "board")
    private Set<BoardMember> boardMembers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "board")
    @OrderBy("position ASC")
    private List<TaskList> taskLists = new ArrayList<>();
}