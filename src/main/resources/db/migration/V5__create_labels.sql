-- V5__create_labels.sql
-- Labels and the task<->label many-to-many join (depends on: boards, tasks)

CREATE TABLE labels (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    board_id        UUID NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    name            VARCHAR(50) NOT NULL,
    color           VARCHAR(20) NOT NULL,
    UNIQUE (board_id, name)
);

CREATE TABLE task_labels (
    task_id         UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    label_id        UUID NOT NULL REFERENCES labels(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, label_id)
);
