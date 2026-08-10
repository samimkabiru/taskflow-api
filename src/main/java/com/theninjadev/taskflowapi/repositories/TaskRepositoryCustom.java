package com.theninjadev.taskflowapi.repositories;

import java.util.UUID;

public interface TaskRepositoryCustom {
    int incrementTaskCounter(UUID boardId);
}