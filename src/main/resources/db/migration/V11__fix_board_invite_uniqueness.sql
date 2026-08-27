
ALTER TABLE board_invites
    DROP CONSTRAINT board_invites_board_id_email_status_key;

CREATE UNIQUE INDEX idx_board_invites_pending_unique
    ON board_invites (board_id, email)
    WHERE status = 'PENDING';