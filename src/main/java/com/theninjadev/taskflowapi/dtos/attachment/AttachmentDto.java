package com.theninjadev.taskflowapi.dtos.attachment;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AttachmentDto(
        UUID id,
        UUID taskId,
        UUID uploadedBy,
        String fileName,
        String contentType,
        long fileSizeBytes,
        OffsetDateTime createdAt
) {}