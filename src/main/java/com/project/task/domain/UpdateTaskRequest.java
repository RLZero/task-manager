package com.project.task.domain;

import com.project.task.domain.entity.TaskPriority;
import com.project.task.domain.entity.TaskStatus;

public record UpdateTaskRequest(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority
) {
}
