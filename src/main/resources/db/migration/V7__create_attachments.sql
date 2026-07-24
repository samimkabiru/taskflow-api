-- V7__create_attachments.sql
-- Task attachments (depends on: tasks, users)

CREATE TABLE attachments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id         UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    uploaded_by     UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    file_name       VARCHAR(255) NOT NULL,
    storage_key     VARCHAR(500) NOT NULL,
    content_type    VARCHAR(100),
    file_size_bytes BIGINT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_attachments_task ON attachments(task_id);
