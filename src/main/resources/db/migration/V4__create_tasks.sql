-- V4__create_tasks.sql
-- Tasks (depends on: task_lists, boards, users)

CREATE TABLE tasks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    board_id        UUID NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    task_list_id    UUID NOT NULL REFERENCES task_lists(id) ON DELETE CASCADE,
    short_code      VARCHAR(20) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    position        DOUBLE PRECISION NOT NULL,
    priority        VARCHAR(10) CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    due_date        DATE,
    assignee_id     UUID REFERENCES users(id) ON DELETE SET NULL,
    created_by      UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (board_id, short_code)
);

CREATE INDEX idx_tasks_list ON tasks(task_list_id);
CREATE INDEX idx_tasks_board ON tasks(board_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
