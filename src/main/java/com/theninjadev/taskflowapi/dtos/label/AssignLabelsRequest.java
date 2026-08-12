package com.theninjadev.taskflowapi.dtos.label;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AssignLabelsRequest {
    private Set<UUID> labelIds;
}