-- Syncs notifications.type check constraint with the updated NotificationType enum
-- (ASSIGNMENT, DUE_SOON, COMMENT, STATUS_CHANGE, INVITE)

-- Clear existing rows using the old type values
TRUNCATE TABLE notifications;

-- Drop the outdated constraint
ALTER TABLE notifications DROP CONSTRAINT notifications_type_check;

-- Re-add it matching the current NotificationType enum
ALTER TABLE notifications ADD CONSTRAINT notifications_type_check
    CHECK (type IN ('ASSIGNMENT', 'DUE_SOON', 'COMMENT', 'STATUS_CHANGE', 'INVITE'));