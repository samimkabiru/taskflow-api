-- V<next>__add_urgent_task_priority.sql
-- Adds URGENT as a valid task priority, matching what the frontend already implemented.
-- Postgres doesn't support altering a CHECK constraint's condition directly —
-- drop the old one and add the replacement.

ALTER TABLE tasks
    DROP CONSTRAINT tasks_priority_check;

ALTER TABLE tasks
    ADD CONSTRAINT tasks_priority_check
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'));