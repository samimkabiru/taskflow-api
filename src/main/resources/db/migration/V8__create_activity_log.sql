-- V8__create_activity_log.sql
-- Append-only activity/audit log (depends on: boards, tasks, users)

CREATE TABLE activity_logs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    board_id        UUID NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    task_id         UUID REFERENCES tasks(id) ON DELETE SET NULL,
    actor_id        UUID REFERENCES users(id) ON DELETE SET NULL,
    action_type     VARCHAR(50) NOT NULL,
    metadata        JSONB NOT NULL DEFAULT '{}',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_activity_board_created ON activity_logs(board_id, created_at DESC);
CREATE INDEX idx_activity_task ON activity_logs(task_id);
