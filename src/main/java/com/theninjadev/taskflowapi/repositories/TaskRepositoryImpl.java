package com.theninjadev.taskflowapi.repositories;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

public class TaskRepositoryImpl implements TaskRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int incrementTaskCounter(UUID boardId) {
        Integer result = jdbcTemplate.queryForObject(
                "UPDATE boards SET task_counter = task_counter + 1 WHERE id = ? RETURNING task_counter",
                Integer.class,
                boardId
        );
        return result;
    }
}