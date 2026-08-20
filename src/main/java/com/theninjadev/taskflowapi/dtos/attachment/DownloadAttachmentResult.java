package com.theninjadev.taskflowapi.dtos.attachment;

public record DownloadAttachmentResult(
        byte[] fileBytes,
        String contentType,
        String fileName
) {
}
