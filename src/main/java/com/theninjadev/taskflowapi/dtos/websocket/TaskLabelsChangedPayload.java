package com.theninjadev.taskflowapi.dtos.websocket;

import com.theninjadev.taskflowapi.dtos.label.LabelDto;

import java.util.List;
import java.util.UUID;

public record TaskLabelsChangedPayload(
        UUID taskId,
        List<LabelDto> labels
) {}