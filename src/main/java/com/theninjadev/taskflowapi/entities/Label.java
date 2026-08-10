package com.theninjadev.taskflowapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "labels")
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks = new LinkedHashSet<>();
}