package com.theninjadev.taskflowapi.scheduled;

import com.theninjadev.taskflowapi.enums.NotificationType;
import com.theninjadev.taskflowapi.repositories.TaskRepository;
import com.theninjadev.taskflowapi.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DueSoonNotificationJob {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;


    @Scheduled(cron = "0 44 9 * * *")
    public void checkDueSoonTasks() {
        for (int offset : new int[] {0, 1, 2}) {
            var targetDate = LocalDate.now().plusDays(offset);
            var dueTasks = taskRepository.findByDueDateAndAssigneeIsNotNull(targetDate);

            dueTasks.forEach(task -> notificationService.notify(NotificationType.DUE_SOON, task.getAssignee(), Map.of("short_code", task.getShortCode(), "task_title", task.getTitle(), "days_until_due", offset)));
        }
    }
}