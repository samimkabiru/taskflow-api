-- V2__create_boards_and_membership.sql
-- Boards and the per-board role model (depends on: users)

CREATE TABLE boards (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    accent_color    VARCHAR(20) NOT NULL DEFAULT 'indigo',
    owner_id        UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    task_counter    INT NOT NULL DEFAULT 0,
    task_prefix     VARCHAR(10) NOT NULL DEFAULT 'TSK',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_boards_owner ON boards(owner_id);

CREATE TABLE board_members (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    board_id        UUID NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role            VARCHAR(20) NOT NULL CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER', 'VIEWER')),
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (board_id, user_id)
);

CREATE INDEX idx_board_members_user ON board_members(user_id);
CREATE INDEX idx_board_members_board ON board_members(board_id);

CREATE TABLE board_invites (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    board_id        UUID NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    email           VARCHAR(255) NOT NULL,
    role            VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MEMBER', 'VIEWER')),
    invited_by      UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REVOKED')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (board_id, email, status)
);
