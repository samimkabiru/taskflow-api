package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.entities.ActivityLog;
import com.theninjadev.taskflowapi.entities.Board;
import com.theninjadev.taskflowapi.entities.Task;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.ActionType;
import com.theninjadev.taskflowapi.repositories.ActivityLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void log(ActionType actionType, Board board, Task task, User actor, Map<String, Object> metadata) {
        var activityLog = new ActivityLog();
        activityLog.setActionType(actionType);
        activityLog.setBoard(board);
        activityLog.setTask(task);
        activityLog.setActor(actor);
        activityLog.setMetadata(metadata != null ? metadata : Map.of());

        activityLogRepository.save(activityLog);
    }
}
