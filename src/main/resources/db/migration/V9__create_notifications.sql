-- V9__create_notifications.sql
-- Notifications (depends on: users)

CREATE TABLE notifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            VARCHAR(50) NOT NULL CHECK (type IN ('TASK_ASSIGNED', 'MENTIONED', 'DUE_SOON', 'BOARD_INVITE', 'BOARD_MEMBER_REMOVED')),
    payload         JSONB NOT NULL DEFAULT '{}',
    is_read         BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_recipient_unread ON notifications(recipient_id, is_read);
